package com.example.funko.category.controller;

import com.example.funko.category.dto.input.InputCategory;
import com.example.funko.category.dto.output.OutputCategory;
import com.example.funko.category.exceptions.CategoryDoesNotExistException;
import com.example.funko.category.mapper.CategoryMapper;
import com.example.funko.category.model.Category;
import com.example.funko.category.service.CategoryService;
import com.example.funko.category.storage.json.CategoryJsonStorageImpl;
import com.example.funko.funko.dto.output.OutputFunko;
import com.example.funko.funko.mapper.FunkoMapper;
import com.example.utils.PageResponse;
import com.example.utils.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Controlador para administrar categorías.
 *
 * @author Diego Novillo Luceño
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    private final CategoryService service;
    private final CategoryJsonStorageImpl categoryJsonStorage;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public CategoryController(
            CategoryService service,
            CategoryJsonStorageImpl categoryJsonStorageImpl,
            PaginationLinksUtils paginationLinksUtils
    ) {
        this.service = service;
        this.categoryJsonStorage = categoryJsonStorageImpl;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    /**
     * Recupera todas las categorías.
     *
     * @return Un ResponseEntity que contiene una lista de todas las categorías.
     */
    @GetMapping
    public ResponseEntity<PageResponse<OutputCategory>> getAllCategories(
            @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        logger.info("Recuperando todas las categorías");
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<OutputCategory> pageResult = service.findAll(PageRequest.of(page, size, sort),isDeleted, name)
                .map(CategoryMapper::toOutputCategory);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Recupera una categoría por su ID.
     *
     * @param id El identificador único de la categoría.
     * @return Un ResponseEntity que contiene la categoría solicitada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OutputCategory> getCategoryById(@PathVariable UUID id) {
        logger.info("Recuperando categoría con ID {}", id);
        return ResponseEntity.ok(
                CategoryMapper.toOutputCategory(
                        service.findById(id)
                )
        );
    }

    /**
     * Recupera una lista de categorías por su nombre.
     *
     * @param name El nombre de las categorías a recuperar.
     * @return Un ResponseEntity que contiene una lista de categorías con el nombre especificado.
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<OutputCategory> getCategoryByName(@PathVariable String name) {
        logger.info("Recuperando categorías por nombre {}", name);
        return ResponseEntity.ok(
                CategoryMapper.toOutputCategory(
                        service.findByName(name)
                )
        );
    }

    /**
     * Guarda una nueva categoría.
     *
     * @param category La nueva categoría a guardar.
     * @return Un ResponseEntity que contiene la categoría guardada.
     */
    @PostMapping
    public ResponseEntity<OutputCategory> save(@Valid @RequestBody InputCategory category) {
        logger.info("Creando una nueva categoría");
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CategoryMapper.toOutputCategory(
                        service.save(category)
                )
        );
    }

    /**
     * Actualiza una categoría existente por su ID.
     *
     * @param id El identificador único de la categoría a actualizar.
     * @param updatedCategory La categoría actualizada.
     * @return Un ResponseEntity que contiene la categoría actualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OutputCategory> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody InputCategory updatedCategory
    ) {
        logger.info("Actualizando categoría con ID {}", id);
        return ResponseEntity.ok(
                CategoryMapper.toOutputCategory(
                        service.update(
                                id,
                                updatedCategory
                        )
                )
        );
    }

    /**
     * Elimina una categoría por su ID.
     *
     * @param id El identificador único de la categoría a eliminar.
     * @param logically Sí se quiere borrar la categoría físicamente o lógicamente
     * @return Un ResponseEntity que contiene la categoría eliminada.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<OutputCategory> deleteCategory(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "true") boolean logically
    ) {
        logger.info("Eliminando categoría con ID {}", id);
        return ResponseEntity.ok(
                CategoryMapper.toOutputCategory(
                        service.delete(id, logically))
        );
    }

    /**
     * Importa las categorías de un fichero JSON.
     *
     * @param file El fichero JSON con las categorías.
     * @return Un ResponseEntity que contiene el resultado de la importación.
    */
    @PostMapping("/importjson")
    public ResponseEntity importCategories(
            @RequestPart("file") MultipartFile file
    ) {
        logger.info("Importando categorias de un fichero json");
        File tempFile;
        try {
            tempFile = File.createTempFile(UUID.randomUUID().toString(), ".json");
            file.transferTo(tempFile);
            LinkedList<InputCategory> list = new LinkedList<>(); // Lista de categorias
            AtomicReference<Boolean> somethingWentWrong = new AtomicReference<>(false);

            categoryJsonStorage.getCategoriesFromFile(tempFile)
                    .doOnNext(category -> {
                            if (category == null) somethingWentWrong.set(true);
                            else list.add(category);
                    }).subscribe();
            tempFile.delete();
            // Si alguna no se ha podido importar
            Boolean result = somethingWentWrong.get();
            if (result) return ResponseEntity.badRequest().body("Hubo un problema al importar el fichero JSON");
            // Ver si ninguna existe en la base de datos
            Category existingCategory = null;
            for (InputCategory category : list) {
                try {
                    existingCategory = service.findByName(category.getName());
                }catch (CategoryDoesNotExistException ignored){};
            }
            if (existingCategory != null) return ResponseEntity.status(HttpStatus.CONFLICT).body("La categoría con nombre: " + existingCategory.getName() + " ya existe");
            // Guardar las categorías en la base de datos
            List<OutputCategory> saved = new LinkedList<>();
            for (InputCategory category : list) {
                saved.add(
                        CategoryMapper.toOutputCategory(
                                service.save(category)
                        )
                );
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        }catch (Exception e){
            logger.error("Error importando el fichero JSON: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}

