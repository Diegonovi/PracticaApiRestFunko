package com.example.funko.funko.services;

import com.example.funko.category.exceptions.CategoryDoesNotExistException;
import com.example.funko.category.model.Category;
import com.example.funko.funko.exceptions.FunkoNotFoundException;
import com.example.funko.funko.model.Funko;
import com.example.funko.category.repository.CategoryRepository;
import com.example.funko.funko.repository.FunkosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * Esta clase implementa la interfaz FunkoService y proporciona métodos para gestionar objetos Funko.
 * Utiliza anotaciones de caché de Spring para almacenar en caché los resultados de los métodos con frecuencia de acceso.
 *
 * @author Diego Novillo Luceño
 */
@Service
@CacheConfig(cacheNames = {"funkos"})
public class FunkoServiceImpl implements FunkoService {

    private final Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);
    private final FunkosRepository repository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructor para FunkoServiceImpl.
     *
     * @param repository       El repositorio FunkosRepository para interactuar con la base de datos de Funko.
     * @param categoryRepository El repositorio CategoryRepository para interactuar con la base de datos de Categoría.
     */
    @Autowired
    public FunkoServiceImpl(FunkosRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Busca un Funko por su ID.
     *
     * @param id El ID del Funko a buscar.
     * @return El Funko con el ID dado, o lanza una FunkoNotFoundException si no se encuentra.
     */
    @Override
    @Cacheable(key = "#id")
    public Funko findById(Long id) {
        logger.info("Buscando el funko con id:" + id);
        return repository.findById(id).orElseThrow(() -> new FunkoNotFoundException("Funko no encontrado para el id: " + id));
    }

    /**
     * Guarda un nuevo Funko o actualiza uno existente.
     *
     * @param funko El Funko a guardar o actualizar.
     * @return El Funko guardado o actualizado.
     * @throws CategoryDoesNotExistException Si la categoría del Funko no existe en la base de datos.
     */
    @Override
    @CachePut(key = "#funko.id")
    public Funko save(Funko funko) {
        logger.info("Guardando el funko: " + funko);
        Optional<Funko> fullFunko = findCategoryInsideFunkoAndUpdateIt(funko);
        if (fullFunko.isPresent()) return repository.save(fullFunko.get());
        else throw new CategoryDoesNotExistException(
                "La categoría " + funko.getCategory().getName()
                + " del funko: " + funko.getName() + " no existe"
        );
    }

    /**
     * Actualiza un Funko existente con nuevos valores.
     *
     * @param id            El ID del Funko a actualizar.
     * @param updatedFunko Los valores nuevos para el Funko.
     * @return El Funko actualizado.
     * @throws FunkoNotFoundException Si el Funko con el ID dado no existe.
     */
    @Override
    @CachePut(key = "#id")
    public Funko update(Long id, Funko updatedFunko) {
        logger.info("Actualizando el funko con id: " + id + ", nuevo valor: " + updatedFunko);
        Optional<Funko> result = repository.findById(id);
        if (result.isPresent()) {
            Funko existingFunko = result.get();
            repository.deleteById(id);
            existingFunko.setName(updatedFunko.getName());
            existingFunko.setPrice(updatedFunko.getPrice());
            existingFunko.setCategory(updatedFunko.getCategory());
            existingFunko.setReleaseDate(updatedFunko.getReleaseDate());
            return save(existingFunko);
        } else throw new FunkoNotFoundException("Funko no encontrado para el id: " + id);
    }

    /**
     * Elimina un Funko por su ID.
     *
     * @param id El ID del Funko a eliminar.
     * @return El Funko eliminado.
     * @throws FunkoNotFoundException Si el Funko con el ID dado no existe.
     */
    @Override
    @CacheEvict(key = "#id")
    public Funko delete(Long id) {
        logger.info("Eliminando el funko con id: " + id);
        Optional<Funko> result = repository.findById(id);
        if (result.isPresent()) {
            repository.deleteById(id);
            return result.get();
        } else throw new FunkoNotFoundException("Funko no encontrado para el id: " + id);
    }

    /**
     * Busca Funkos por su nombre.
     *
     * @param name El nombre de los Funkos a buscar.
     * @return Una lista de Funkos con el nombre dado.
     */
    @Override
    public List<Funko> findByName(String name) {
        logger.info("Buscando el funko con nombre: " + name);
        return repository.findByName(name);
    }

    /**
     * Busca todos los Funkos.
     *
     * @return Una lista de todos los Funkos.
     */
    @Override
    public List<Funko> findAll() {
        logger.info("Buscando todos los funkos");
        return repository.findAll();
    }

    /**
     * Busca la categoría de un Funko y actualiza el Funko con el objeto Categoría completo.
     *
     * @param funko El Funko a actualizar.
     * @return Un Optional que contiene el Funko actualizado si se encuentra la categoría, o un Optional vacío de lo contrario.
     */
    private Optional<Funko> findCategoryInsideFunkoAndUpdateIt(Funko funko) {
        Optional<Category> foundCategory = categoryRepository.findByName(funko.getCategory().getName());
        if (foundCategory.isPresent()) {
            funko.setCategory(foundCategory.get());
            return Optional.of(funko);
        } else return Optional.empty();
    }
}
