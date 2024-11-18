package com.example.funko.funko.mapper;

import com.example.funko.funko.dto.input.InputFunko;
import com.example.funko.funko.dto.output.OutputFunko;
import com.example.funko.funko.model.Funko;
/**
 * Esta clase proporciona métodos para mapear objetos Funko y OutputFunko.
 */
public class FunkoMapper {
    /**
     * Constructor privado para evitar la creación de instancias de esta clase.
     */
    private FunkoMapper() {}

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
               .stock(input.getStock())
               .createdAt(input.getCreatedAt().toString())
               .updatedAt(input.getUpdatedAt().toString())
               .build();
    }

    public static InputFunko toInputFunko(Funko funko){
        return InputFunko.builder()
               .name(funko.getName())
               .price(funko.getPrice())
               .releaseDate(funko.getReleaseDate())
               .category(funko.getCategory().getName())
               .build();
    }
}