package com.example.funko.funko.services;

import com.example.funko.category.exceptions.CategoryDoesNotExistException;
import com.example.funko.category.model.Category;
import com.example.funko.category.service.CategoryService;
import com.example.funko.funko.dto.input.InputFunko;
import com.example.funko.funko.dto.output.OutputFunko;
import com.example.funko.funko.exceptions.FunkoNotFoundException;
import com.example.funko.funko.mapper.FunkoMapper;
import com.example.funko.funko.model.Funko;
import com.example.funko.funko.repository.FunkosRepository;
import com.example.funko.websocket.config.WebSocketConfig;
import com.example.funko.websocket.config.WebSocketHandler;
import com.example.funko.websocket.notifications.model.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDateTime;
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
    private final CategoryService categoryService;
    private final WebSocketConfig webSocketConfig;
    private WebSocketHandler webSocketService;
    private final ObjectMapper mapper = new ObjectMapper();



    /**
     * Constructor para FunkoServiceImpl.
     *
     * @param repository       El repositorio FunkosRepository para interactuar con la base de datos de Funko.
     * @param categoryService El servicio CategoryService para trabajar con la identidad Categoría.
     */
    @Autowired
    public FunkoServiceImpl(
            FunkosRepository repository,
            CategoryService categoryService,
            WebSocketConfig config,
            WebSocketHandler handler
    ) {
        this.repository = repository;
        this.categoryService = categoryService;
        this.webSocketConfig = config;
        this.webSocketService = webSocketConfig.webSocketFunkosHandler();
        mapper.registerModule(new JavaTimeModule());
    }

    /**
     * Busca un Funko por su ID.
     *
     * @param id El ID del Funko a buscar.
     * @return El Funko con el ID dado, o lanza una FunkoNotFoundException si no se encuentra.
     */
    @Override
    @Cacheable
    public Funko findById(Long id) {
        logger.info("Buscando el funko con id: {}", id);
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
    @CachePut(key = "#result.id")
    public Funko save(InputFunko funko) {
        logger.info("Guardando el funko: {}", funko);
        Category category = categoryService.findByName(funko.getCategory());
        Funko fullFunko = new Funko();
        fullFunko.setName(funko.getName());
        fullFunko.setPrice(funko.getPrice());
        fullFunko.setReleaseDate(funko.getReleaseDate());
        fullFunko.setStock(funko.getStock());
        fullFunko.setCategory(category);

        onChange( // Manda la notificación
                Notification.Tipo.CREATE,
                fullFunko
                );
        return repository.save(fullFunko);
    }

    /**
     * Actualiza un Funko existente con nuevos valores.
     *
     * @param id El ID del Funko a actualizar.
     * @param updatedFunko Los valores nuevos para el Funko.
     * @return El Funko actualizado.
     * @throws FunkoNotFoundException Si el Funko con el ID dado no existe.
     */
    @Override
    @CachePut(key = "#id")
    public Funko update(Long id, InputFunko updatedFunko) {
        logger.info("Actualizando el funko con id: {}, nuevo valor: {}", id, updatedFunko);
        Optional<Funko> result = repository.findById(id);
        if (result.isPresent()) {
            Funko existingFunko = result.get();
            existingFunko.setName(updatedFunko.getName());
            existingFunko.setPrice(updatedFunko.getPrice());
            existingFunko.setReleaseDate(updatedFunko.getReleaseDate());
            existingFunko.setUpdatedAt(LocalDateTime.now());
            Funko validFunko = findCategoryInsideFunkoAndUpdateIt(existingFunko, updatedFunko.getCategory());
            onChange( // Manda la notificación
                    Notification.Tipo.UPDATE,
                    existingFunko
            );
            return repository.save(validFunko);
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
        logger.info("Eliminando el funko con id: {}",id);
        Optional<Funko> result = repository.findById(id);
        if (result.isPresent()) {
            repository.deleteById(id);
            onChange( // Manda la notificación
                    Notification.Tipo.DELETE,
                    result.get()
            );
            return result.get();
        } else throw new FunkoNotFoundException("Funko no encontrado para el id: " + id);
    }

    /**
     * Busca todos los Funkos.
     *
     * @return Una lista de todos los Funkos.
     */
    @Override
    public Page<Funko> findAll(
            Pageable pageable,
            Optional<String> category,
            Optional<String> name,
            Optional<Double> maxPrice,
            Optional<Integer> minStock
    ) {
        logger.info("Buscando todos los funkos");
        // Criterio de búsqueda por categoría
        Specification<Funko> categorySpec = (root, query, criteriaBuilder) ->
                category.map(c -> {
                    Join<Funko, Category> categoriaJoin = root.join("category"); // Join con categoría
                    return criteriaBuilder.like(criteriaBuilder.lower(categoriaJoin.get("name")), "%" + c.toLowerCase() + "%"); // Buscamos por nombre
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true))); // Si no hay categoría, no filtramos
        // Criterio de búsqueda por nombre
        Specification<Funko> specModeloProducto = (root, query, criteriaBuilder) ->
                name.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por precioMax, es decir tiene que ser menor o igual
        Specification<Funko> maxPriceSpec = (root, query, criteriaBuilder) ->
                maxPrice.map(p -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), p))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        // Criterio de búsqueda por stockMin, es decir tiene que ser menor o igual
        Specification<Funko> minStockSpec = (root, query, criteriaBuilder) ->
                minStock.map(s -> criteriaBuilder.lessThanOrEqualTo(root.get("stock"), s))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> criterio = Specification.where(categorySpec)
                .and(specModeloProducto)
                .and(maxPriceSpec)
                .and(minStockSpec);
        return repository.findAll(criterio, pageable);
    }

    /**
     * Busca la categoría de un Funko y actualiza el Funko con el objeto Categoría completo.
     *
     * @param funko El Funko a actualizar.
     * @return Un Optional que contiene el Funko actualizado si se encuentra la categoría, o un Optional vacío de lo contrario.
     */
    private Funko findCategoryInsideFunkoAndUpdateIt(
            Funko funko,
            String category
    ) {
        Category foundCategory = categoryService.findByName(category);
        Category newCategory = new Category();
        newCategory.setId(foundCategory.getId());
        newCategory.setName(foundCategory.getName());
        funko.setCategory(newCategory);
        return funko;
    }

    void onChange(Notification.Tipo tipo, Funko data) {
        logger.debug("Servicio de productos onChange con tipo: {} y datos: {}",tipo,data);

        if (webSocketService == null) {
            logger.warn("No se ha podido enviar la notificación a los clientes ws, no se ha encontrado el servicio");
            webSocketService = this.webSocketConfig.webSocketFunkosHandler();
        }

        try {

            Notification<OutputFunko> notificacion = new Notification<>(
                    "FUNKOS",
                    tipo,
                    FunkoMapper.toOutputFunko(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString((notificacion));

            logger.info("Enviando mensaje a los clientes ws");
            // Enviamos el mensaje a los clientes ws con un hilo, si hay muchos clientes, puede tardar
            // no bloqueamos el hilo principal que atiende las peticiones http
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    logger.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            logger.error("Error al convertir la notificación a JSON", e);
        }
    }
}
