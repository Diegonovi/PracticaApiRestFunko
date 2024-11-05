package com.example.funko.category.service;

import com.example.funko.category.model.Category;
import com.example.funko.funko.exceptions.FunkoNotFoundException;
import com.example.funko.funko.model.Funko;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    Category findById(UUID id) throws FunkoNotFoundException;

    Category save(Category category);

    Category update(UUID id, Category updatedCategory);

    Category delete(UUID id, Boolean logically);

    Category findByName(String name);

    List<Category> findAll();
}
