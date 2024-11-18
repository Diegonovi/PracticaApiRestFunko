package com.example.funko.category.service;

import com.example.funko.category.dto.input.InputCategory;
import com.example.funko.category.model.Category;
import com.example.funko.funko.exceptions.FunkoNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryService {
    Category findById(UUID id) throws FunkoNotFoundException;

    Category save(InputCategory category);

    Category update(UUID id, InputCategory updatedCategory);

    Category delete(UUID id, Boolean logically);

    Category findByName(String name);

    Page<Category> findAll(
            Pageable pageable,
            Optional<Boolean> isDeleted,
            Optional<String> name
    );
}
