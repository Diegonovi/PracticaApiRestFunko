package com.example.funko.category.mapper;

import com.example.funko.category.dto.input.InputCategory;
import com.example.funko.category.dto.output.OutputCategory;
import com.example.funko.category.model.Category;

/**
 * Esta clase proporciona métodos para mapear objetos de modelo de categoría a DTOs y viceversa.
 */
public class CategoryMapper {

    /**
     * Convierte un objeto de modelo de categoría en un DTO de OutputCategory.
     *
     * @param category El objeto de modelo de categoría que se va a convertir.
     * @return Un DTO de OutputCategory que contiene los datos del objeto de modelo de categoría de entrada.
     */
    public static OutputCategory toOutputCategory(Category category) {
        OutputCategory outputCategory = new OutputCategory();
        outputCategory.setId(category.getId().toString());
        outputCategory.setName(category.getName());
        outputCategory.setCreatedAt(category.getCreatedAt().toString());
        outputCategory.setUpdatedAt(category.getUpdatedAt().toString());
        outputCategory.setIsDeleted(category.getIsDeleted());
        return outputCategory;
    }

    /**
     * Convierte un DTO de InputCategory en un objeto de modelo de categoría.
     *
     * @param inputCategory El DTO de InputCategory que se va a convertir.
     * @return Un objeto de modelo de categoría que contiene los datos del DTO de InputCategory de entrada.
     */
    public static Category toCategory(InputCategory inputCategory) {
        Category category = new Category();
        category.setName(inputCategory.getName());
        return category;
    }
}
