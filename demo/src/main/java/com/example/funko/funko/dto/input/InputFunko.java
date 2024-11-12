package com.example.funko.funko.dto.input;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InputFunko {
    private Long id = 0L;
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    @NotNull(message = "El precio no puede estar vacío")
    @Min(value = 0, message = "El precio no puede ser menor de 0")
    private Double price;
    @NotNull(message = "La cantidad no puede estar vacía")
    @Min(value = 0, message = "La cantidad no puede ser menor de 0")
    private Integer stock;
    @Past
    @NotNull(message = "La fecha de lanzamiento no puede estar vacía")
    private LocalDate releaseDate;
    @NotEmpty(message = "La categoría no puede estar vacía")
    private String category;
}
