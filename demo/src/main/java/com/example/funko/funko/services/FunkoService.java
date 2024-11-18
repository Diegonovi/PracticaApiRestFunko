package com.example.funko.funko.services;

import com.example.funko.funko.dto.input.InputFunko;
import com.example.funko.funko.exceptions.FunkoNotFoundException;
import com.example.funko.funko.model.Funko;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FunkoService {
    Funko findById(Long id) throws FunkoNotFoundException;

    Funko save(InputFunko funko);

    Funko update(Long id, InputFunko updatedFunko);

    Funko delete(Long id);

    Page<Funko> findAll(
        Pageable pageable,
        Optional<String> category,
        Optional<String> name,
        Optional<Double> maxPrice,
        Optional<Integer> minStock
    );

}
