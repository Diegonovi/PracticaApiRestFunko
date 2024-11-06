package com.example.funko.category.storage.json;

import com.example.funko.category.dto.input.InputCategory;
import com.example.funko.category.mapper.CategoryMapper;
import com.example.funko.category.model.Category;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class CategoryJsonStorageImpl implements CategoryJsonStorage{
    
    /**
     * Este método lee un archivo JSON y devuelve un Flux de objetos de la categoría.
     *
     * @param file El archivo JSON que se va a leer. El archivo debe contener un array de objetos JSON,
     *             cada uno representando una categoría.
     * @return Un Flux de objetos de la categoría. Cada objeto se crea a partir de un objeto JSON en el archivo de entrada.
     *         Si se produce un error durante la lectura o el análisis del archivo, se emite una señal de error.
     */
    @Override
    public Flux<Category> getCategoriesFromFile(File file) {
        return Flux.create(fluxSink -> {
                try{
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode list = objectMapper.readTree(file);
                    if (list.isArray()) {
                        for (JsonNode node : list) {
                            InputCategory category = objectMapper.treeToValue(node, InputCategory.class);
                            fluxSink.next(CategoryMapper.toCategory(category));
                        }
                    }
                } catch (IOException e){
                    fluxSink.error(e);
                }
        });
    }

    @Override
    public void saveCategoriesToFile(File file, List<Category> categories) {
        // TODO
    }
}
