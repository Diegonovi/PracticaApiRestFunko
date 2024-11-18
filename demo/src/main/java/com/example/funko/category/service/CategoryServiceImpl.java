package com.example.funko.category.service;

import com.example.funko.category.dto.input.InputCategory;
import com.example.funko.category.exceptions.CategoryAlreadyExistsException;
import com.example.funko.category.exceptions.CategoryDoesNotExistException;
import com.example.funko.category.exceptions.CategoryException;
import com.example.funko.category.exceptions.CategoryHasFunkosException;
import com.example.funko.category.model.Category;
import com.example.funko.category.model.Description;
import com.example.funko.category.repository.CategoryRepository;
import com.example.funko.category.storage.json.CategoryJsonStorage;
import com.example.funko.funko.model.Funko;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Join;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
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
    private final CategoryJsonStorage categoryJsonStorage;

    /**
     * Constructor que inyecta la dependencia CategoryRepository.
     */
    @Autowired
    public CategoryServiceImpl(
            CategoryRepository categoryRepository,
            CategoryJsonStorage categoryJsonStorage
    ) {
        this.categoryJsonStorage = categoryJsonStorage;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Inicializa la categoría con algunos datos de prueba.
     * Este método se ejecuta solo una vez al iniciar la aplicación.
     */
    @PostConstruct
    public void init() {
        logger.info("Inicializando categorías con datos de prueba");
        try {
            URL location = ClassLoader.getSystemResource("data/categories.json");
            File categoryJsonFile = new File(location.getPath());
            categoryJsonStorage.getCategoriesFromFile(categoryJsonFile)
                    .doOnNext(this::save)
                    .subscribe();
        }catch (Exception e){
            logger.error("Error al inicializar las categorías con datos de prueba", e);
        }
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
        logger.info("Buscando la categoría con id: {}", id);
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
    public Category save(InputCategory category) {
        logger.info("Guardando la categoría: {}", category);
        Category newCategory = new Category();
        Description newDescription = new Description();
        newDescription.setText(category.getDescription());
        newCategory.setName(category.getName());
        newCategory.setDescription(newDescription);
        if (categoryRepository.findByName(category.getName()).isPresent()){
            throw new CategoryAlreadyExistsException("Ya existe una categoría con el nombre: " + category.getName());
        }
        return categoryRepository.save(newCategory);
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
    public Category update(UUID id, InputCategory updatedCategory) {
        logger.info("Actualizando la categoría con id: {}",id);
        Optional<Category> result = categoryRepository.findById(id);
        Optional<Category> result2 = categoryRepository.findByName(updatedCategory.getName());

        if (result.isPresent()) { // Si existe esa categoria
            if (result2.isPresent() && result2.get().getId() != id) { // Si existe una categoría con ese nombre
                // Si la categoría que estás intentando actualizar tiene un nombre que ya existe en la BBDD
                throw new CategoryAlreadyExistsException("Ya existe una categoría con el nombre " + updatedCategory.getName());
            }
            Category existingCategory = result.get();
            existingCategory.setName(updatedCategory.getName());
            // Si la descripción es diferente a la que tenía
            if (!result.get().getDescription().getText().equals(updatedCategory.getDescription())){
                Description description = new Description();
                description.setText(updatedCategory.getDescription());
                description.setUpdatedAt(LocalDateTime.now());
                description.setCreatedAt(result.get().getDescription().getCreatedAt());
                existingCategory.setDescription(description);
            }
            return categoryRepository.save(existingCategory);
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
        logger.info("Eliminando la categoría con id: {}",id);
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new CategoryDoesNotExistException("Error al borrar la categoría con id: " + id);
        } else if (!category.get().getFunkos().isEmpty()){
            throw new CategoryHasFunkosException("Esta categoría tiene funkos, no se puede borrar");
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
        logger.info("Buscando las categorías con nombre: {}", name);
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
    public Page<Category> findAll(
            Pageable pageable,
            Optional<Boolean> isDeleted,
            Optional<String> name
    ) {
        logger.info("Buscando todas las categorías");
        // Criterio de búsqueda por nombre
        Specification<Category> nameSpec = (root, query, criteriaBuilder) ->
                name.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por isDeleted
        // Criterio de búsqueda por isDeleted
        Specification<Category> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(d -> criteriaBuilder.equal(root.get("isDeleted"), d))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Category> criterio = Specification.where(nameSpec)
                .and(specIsDeleted);
        return categoryRepository.findAll(criterio, pageable);
    }
}
