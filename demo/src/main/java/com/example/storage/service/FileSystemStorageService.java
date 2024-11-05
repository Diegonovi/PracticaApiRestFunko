package com.example.storage.service;

import com.example.storage.controller.FilesController;
import com.example.storage.exceptions.StorageException;
import com.example.storage.exceptions.StorageFileNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService{

    @Value("${images.Storage}")
    private Path rootLocation;

    @Override
    @PostConstruct
    public void init() {

    }

    @Override
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }

            return destinationFile.getFileName().toString();
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    /**
     * Método que es capaz de cargar un fichero a partir de su nombre
     * Devuelve un objeto de tipo Resource
     */
    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("No se puede leer fichero: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("No se puede leer fichero: " + filename + " " + e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    /**
     * Método que devuelve la URL de un fichero a partir de su nombre
     * Devuelve un objeto de tipo String
     */
    @Override
    public String getUrl(String filename) {
        return MvcUriComponentsBuilder
                // El segundo argumento es necesario solo cuando queremos obtener la imagen
                // En este caso tan solo necesitamos obtener la URL
                .fromMethodName(FilesController.class, "serveFile", filename, null)
                .build().toUriString();
    }
}
