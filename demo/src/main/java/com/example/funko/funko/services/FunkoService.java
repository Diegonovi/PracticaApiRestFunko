package com.example.funko.funko.services;

import com.example.funko.funko.exceptions.FunkoNotFoundException;
import com.example.funko.funko.model.Funko;

import java.util.List;

public interface FunkoService {
    Funko findById(Long id) throws FunkoNotFoundException;

    Funko save(Funko funko);

    Funko update(Long id, Funko updatedFunko);

    Funko delete(Long id);

    List<Funko> findByName(String name);

    List<Funko> findAll();
}
