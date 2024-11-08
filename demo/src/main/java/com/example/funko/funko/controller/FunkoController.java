package com.example.funko.funko.controller;

import com.example.funko.funko.dto.input.InputFunko;
import com.example.funko.funko.dto.output.OutputFunko;
import com.example.funko.funko.mapper.FunkoMapper;
import com.example.funko.funko.services.FunkoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


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

    @Autowired
    public FunkoController(FunkoService service) {
        this.service = service;
    }

    /**
     * Recupera una lista de todas las figuras de Funko.
     *
     * @return Un ResponseEntity que contiene una lista de figuras de Funko con sus nombres de categorías asociadas.
     */
    @GetMapping
    public ResponseEntity<List<OutputFunko>> getAllFunkos() {
        logger.info("Buscando todos los funkos");
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(FunkoMapper::toOutputFunko).toList()
        );
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
     * Recupera una lista de figuras de Funko por su nombre.
     *
     * @param name El nombre de las figuras de Funko que se van a recuperar.
     * @return Un ResponseEntity que contiene una lista de figuras de Funko con sus nombres de categorías asociados.
     */
    @GetMapping("name/{name}")
    public ResponseEntity<List<OutputFunko>> getFunkosByNombre(@PathVariable String name) {
        logger.info("Fetching Funkos by nombre {}", name);
        return ResponseEntity.ok(
                service.findByName(name).stream()
                        .map(FunkoMapper::toOutputFunko)
                        .toList()
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
                        service.save(
                                FunkoMapper.toFunko(funko)
                        )
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
                        service.update(
                                id,
                                FunkoMapper.toFunko(updatedFunko)
                        )
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

