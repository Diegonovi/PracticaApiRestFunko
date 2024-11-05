package com.example.funko.funko.mapper;

import com.example.funko.category.model.Category;
import com.example.funko.funko.dto.input.InputFunko;
import com.example.funko.funko.dto.output.OutputFunko;
import com.example.funko.funko.model.Funko;
import jakarta.validation.Valid;

import java.time.LocalDate;

/**
 * Esta clase proporciona métodos para mapear objetos Funko y OutputFunko.
 */
public class FunkoMapper {

    /**
     * Convierte un objeto OutputFunko a un objeto Funko.
     *
     * @param input El objeto InputFunko que se va a convertir.
     * @return Un nuevo objeto Funko con los mismos datos que el input, pero con un objeto Category en lugar de un nombre de categoría.
     * @throws jakarta.validation.ValidationException Si el objeto input no es válido.
     */
    public static @Valid Funko toFunko(InputFunko input){
        Funko newFunko = new Funko();
        newFunko.setName(input.getName());
        newFunko.setPrice(input.getPrice());
        newFunko.setReleaseDate(input.getReleaseDate());
        newFunko.setCategory(Category.builder()
                .name(input.getCategory())
                .build());
        return newFunko;
    }

    /**
     * Convierte un objeto Funko a un objeto FunkoOutput.
     *
     * @param input El objeto Funko que se va a convertir.
     * @return Un nuevo objeto FunkoOutput con los mismos datos que el input, pero con un nombre de categoría en lugar de un objeto Category.
     */
    public static OutputFunko toOutputFunko(Funko input){
        return OutputFunko.builder()
               .id(input.getId())
               .name(input.getName())
               .releaseDate(input.getReleaseDate())
               .category(input.getCategory().getName())
               .price(input.getPrice())
               .createdAt(input.getCreatedAt().toString())
               .updatedAt(input.getUpdatedAt().toString())
               .build();
    }
}