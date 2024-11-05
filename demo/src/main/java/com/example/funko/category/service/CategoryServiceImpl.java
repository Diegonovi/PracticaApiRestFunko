package com.example.funko.category.service;

import com.example.funko.category.exceptions.CategoryAlreadyExistsException;
import com.example.funko.category.exceptions.CategoryDoesNotExistException;
import com.example.funko.category.exceptions.CategoryException;
import com.example.funko.category.model.Category;
import com.example.funko.category.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementa la interfaz CategoryService y proporciona métodos para administrar categorías.
 * Esta clase está anotada con @Service para indicar que es un componente de servicio de Spring.
 * También utiliza la caché con anotaciones como @CacheConfig, @Cacheable, @CachePut y @CacheEvict.
 */
@Service
@CacheConfig(cacheNames = {"categories"})
public class CategoryServiceImpl implements CategoryService {

    private final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryRepository categoryRepository;

    /**
     * Constructor que inyecta la dependencia CategoryRepository.
     */
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Busca y devuelve una categoría por su identificador único.
     *
     * @param id El identificador único de la categoría.
     * @return La categoría con el identificador especificado.
     * @throws CategoryDoesNotExistException Si no se encuentra una categoría con el identificador especificado.
     */
    @Override
    @Cacheable(key = "#id")
    public Category findById(UUID id) {
        logger.info("Buscando la categoría con id: " + id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryDoesNotExistException("Categoría no encontrada para el id: " + id));
    }

    /**
     * Guarda una nueva categoría en la base de datos.
     *
     * @param category La categoría que se va a guardar.
     * @return La categoría guardada con su identificador único generado.
     */
    @Override
    @CachePut(key = "#category.id")
    public Category save(Category category) {
        logger.info("Guardando la categoría: " + category);
        if (!categoryRepository.findByName(category.getName()).isEmpty()){
            throw new CategoryAlreadyExistsException("Ya existe una categoría con el nombre: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    /**
     * Actualiza una categoría existente en la base de datos.
     *
     * @param id El identificador único de la categoría que se va a actualizar.
     * @param updatedCategory La categoría con los nuevos valores.
     * @return La categoría actualizada.
     * @throws CategoryDoesNotExistException Si no se encuentra una categoría con el identificador especificado.
     * @throws CategoryException Si se produce un error durante la actualización.
     */
    @Override
    @CachePut(key = "#id")
    public Category update(UUID id, Category updatedCategory) {
        logger.info("Actualizando la categoría con id: " + id + ", nuevo valor: " + updatedCategory);
        Optional<Category> result = categoryRepository.findById(id);
        Optional<Category> result2 = categoryRepository.findByName(updatedCategory.getName());

        if (result.isPresent()) { // Si existe esa categoria
            if (result2.isPresent()) { // Si existe una categoría con ese nombre
                // Si la categoría que estas intentando actualizar tiene un nombre que ya existe en la BBDD
                if (result2.get().getId() != id) throw new CategoryAlreadyExistsException("Ya existe una categoría con el nombre " + updatedCategory.getName());
            }
            Category existingCategory = result.get();
            categoryRepository.deleteById(id);
            existingCategory.setName(updatedCategory.getName());
            return save(existingCategory);
        } else throw new CategoryDoesNotExistException("Categoria no encontrada para el id: " + id);
    }

    /**
     * Elimina una categoría de la base de datos.
     *
     * @param id El identificador único de la categoría que se va a eliminar.
     * @param logically Si se quiere hacer un borrado físico de la categoria o un o uno lógico
     * @return La categoría eliminada.
     * @throws CategoryDoesNotExistException Si no se encuentra una categoría con el identificador especificado.
     * @throws CategoryException Si se produce un error durante la eliminación.
     */
    @Override
    @CacheEvict(key = "#id")
    public Category delete(UUID id, Boolean logically) {
        logger.info("Eliminando la categoría con id: " + id);
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new CategoryDoesNotExistException("Error al actualizar la categoría con id: " + id);
        }
        Category validCategory = category.get();
        validCategory.setIsDeleted(true);
        validCategory.setUpdatedAt(LocalDateTime.now());
        categoryRepository.deleteById(id);
        if (logically){
            categoryRepository.save(validCategory);
        }
        return validCategory;
    }

    /**
     * Busca y devuelve una categoría por su nombre.
     *
     * @param name El nombre de la categoría.
     * @return La categoría con el nombre especificado.
     * @throws CategoryDoesNotExistException Si no se encuentra una categoría con el nombre especificado.
     */
    @Override
    public Category findByName(String name) {
        logger.info("Buscando las categorías con nombre: " + name);
        Optional<Category> result = categoryRepository.findByName(name);
        if (result.isPresent()) {
            return result.get();
        } else throw new CategoryDoesNotExistException("Categoría no encontrada para el nombre: " + name);
    }

    /**
     * Busca y devuelve todas las categorías.
     *
     * @return Una lista de todas las categorías.
     */
    @Override
    public List<Category> findAll() {
        logger.info("Buscando todas las categorías");
        return categoryRepository.findAll();
    }
}
