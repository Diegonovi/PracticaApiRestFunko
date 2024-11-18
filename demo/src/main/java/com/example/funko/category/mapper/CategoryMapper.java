package com.example.funko.category.mapper;

import com.example.funko.category.dto.input.InputCategory;
import com.example.funko.category.dto.output.OutputCategory;
import com.example.funko.category.model.Category;
import com.example.funko.category.model.Description;

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
        outputCategory.setDescription(category.getDescription().getText());
        outputCategory.setCreatedAt(category.getCreatedAt().toString());
        outputCategory.setUpdatedAt(category.getUpdatedAt().toString());
        outputCategory.setIsDeleted(category.getIsDeleted());
        return outputCategory;
    }
}
