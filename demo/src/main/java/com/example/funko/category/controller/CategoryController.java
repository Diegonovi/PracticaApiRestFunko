package com.example.funko.category.controller;

import com.example.funko.category.dto.input.InputCategory;
import com.example.funko.category.dto.output.OutputCategory;
import com.example.funko.category.mapper.CategoryMapper;
import com.example.funko.category.model.Category;
import com.example.funko.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @Autowired
    public CategoryController(CategoryService service) {
        this.service = service;
    }

    /**
     * Recupera todas las categorías.
     *
     * @return Un ResponseEntity que contiene una lista de todas las categorías.
     */
    @GetMapping
    public ResponseEntity<List<OutputCategory>> getAllCategories() {
        logger.info("Recuperando todas las categorías");
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(CategoryMapper::toOutputCategory)
                        .toList()
        );
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
                        service.save(
                                CategoryMapper.toCategory(category)
                        )
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
                                CategoryMapper.toCategory(updatedCategory)
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
}

