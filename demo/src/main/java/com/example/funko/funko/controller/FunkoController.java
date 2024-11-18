package com.example.funko.funko.controller;

import com.example.funko.funko.dto.input.InputFunko;
import com.example.funko.funko.dto.output.OutputFunko;
import com.example.funko.funko.mapper.FunkoMapper;
import com.example.funko.funko.services.FunkoService;
import com.example.utils.PageResponse;
import com.example.utils.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;


/**
 * Esta clase se encarga de manejar las solicitudes HTTP relacionadas con las figuras de Funko.
 * Proporciona puntos finales para recuperar, crear, actualizar y eliminar registros de Funko.
 *
 * @author Diego Novillo Luceño
 */
@RestController
@RequestMapping("/funkos")
public class FunkoController {
    private final Logger logger = LoggerFactory.getLogger(FunkoController.class);

    private final FunkoService service;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public FunkoController(
            FunkoService service,
            PaginationLinksUtils paginationLinksUtils
    ) {
        this.service = service;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    /**
     * Recupera una lista de todas las figuras de Funko.
     *
     * @return Un ResponseEntity que contiene una lista de figuras de Funko con sus nombres de categorías asociadas.
     */
    @GetMapping
    public ResponseEntity<PageResponse<OutputFunko>> getAllFunkos(
            @RequestParam(required = false) Optional<String> category,
            @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<Double> maxPrice,
            @RequestParam(required = false) Optional<Integer> minStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        logger.info("Buscando todos los funkos");
        // Creamos el objeto de ordenación
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<OutputFunko> pageResult = service.findAll(PageRequest.of(page, size, sort), category, name, maxPrice, minStock)
                .map(FunkoMapper::toOutputFunko);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    /**
     * Recupera una figura de Funko específica por su ID.
     *
     * @param id El ID de la figura de Funko que se va a recuperar.
     * @return Un ResponseEntity que contiene la figura de Funko solicitada con su nombre de categoría asociado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OutputFunko> getFunkoById(@PathVariable Long id) {
        logger.info("Buscando el funko con id:" + id);
        return ResponseEntity.ok(
                FunkoMapper.toOutputFunko(
                        service.findById(id)
                )
        );
    }

    /**
     * Crea una nueva figura de Funko.
     *
     * @param funko La figura de Funko que se va a crear.
     * @return Un ResponseEntity que contiene la figura de Funko recién creada con su nombre de categoría asociado.
     */
    @PostMapping
    public ResponseEntity<OutputFunko> save(@Valid @RequestBody InputFunko funko) {
        logger.info("Creando un nuevo funko");
        return ResponseEntity.status(HttpStatus.CREATED).body(
                FunkoMapper.toOutputFunko(
                        service.save(funko)
                )
        );
    }

    /**
     * Actualiza una figura de Funko existente por su ID.
     *
     * @param id El ID de la figura de Funko que se va a actualizar.
     * @param updatedFunko La figura de Funko actualizada.
     * @return Un ResponseEntity que contiene la figura de Funko actualizada con su nombre de categoría asociado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<OutputFunko> updateFunko(
            @PathVariable Long id,
            @Valid @RequestBody InputFunko updatedFunko
    ) {
        logger.info("Uctualizando Funko con ID {}", id);
        return ResponseEntity.ok(
                FunkoMapper.toOutputFunko(
                        service.update(id, updatedFunko)
                )
        );
    }

    /**
     * Elimina una figura de Funko por su ID.
     *
     * @param id El ID de la figura de Funko que se va a eliminar.
     * @return Un ResponseEntity que contiene la figura de Funko eliminada con su nombre de categoría asociado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<OutputFunko> deleteFunko(@PathVariable Long id) {
        logger.info("Borrando Funko con ID {}", id);
        return ResponseEntity.ok(
                FunkoMapper.toOutputFunko(
                        service.delete(id))
        );
    }
}

