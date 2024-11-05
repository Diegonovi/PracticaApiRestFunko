package com.example.funko.funko.dto.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
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
    @Min(value = 0, message = "El precio no puede ser menor de 0")
    private Double price;
    @Past
    private LocalDate releaseDate;
    @NotEmpty(message = "La categoría no puede estar vacía")
    private String category;
}
