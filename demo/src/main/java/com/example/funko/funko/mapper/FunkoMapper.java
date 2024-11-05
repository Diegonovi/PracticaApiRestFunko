package com.example.funko.funko.mapper;

import com.example.funko.category.model.Category;
import com.example.funko.funko.model.Funko;
import com.example.funko.funko.model.FunkoWithCategoryName;
import jakarta.validation.Valid;

import java.time.LocalDate;

/**
 * Esta clase proporciona métodos para mapear objetos Funko y FunkoWithCategoryName.
 */
public class FunkoMapper {

    /**
     * Convierte un objeto FunkoWithCategoryName a un objeto Funko.
     *
     * @param input El objeto FunkoWithCategoryName que se va a convertir.
     * @return Un nuevo objeto Funko con los mismos datos que el input, pero con un objeto Category en lugar de un nombre de categoría.
     * @throws jakarta.validation.ValidationException Si el objeto input no es válido.
     */
    public static @Valid Funko toFunko(FunkoWithCategoryName input){
        Funko newFunko = new Funko();
        newFunko.setName(input.getName());
        newFunko.setPrice(input.getPrice());
        newFunko.setReleaseDate(LocalDate.parse(input.getReleaseDate()));
        newFunko.setCategory(Category.builder()
                .name(input.getCategory())
                .build());
        return newFunko;
    }

    /**
     * Convierte un objeto Funko a un objeto FunkoWithCategoryName.
     *
     * @param input El objeto Funko que se va a convertir.
     * @return Un nuevo objeto FunkoWithCategoryName con los mismos datos que el input, pero con un nombre de categoría en lugar de un objeto Category.
     */
    public static FunkoWithCategoryName toFunkoWithCategoryName(Funko input){
        return FunkoWithCategoryName.builder()
               .id(input.getId())
               .name(input.getName())
               .releaseDate(input.getReleaseDate().toString())
               .category(input.getCategory().getName())
               .price(input.getPrice())
               .build();
    }
}