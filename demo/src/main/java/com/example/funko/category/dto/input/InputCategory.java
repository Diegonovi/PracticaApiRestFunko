package com.example.funko.category.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputCategory {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;
    @NotBlank(message = "La categoría debe tener una descripción")
    private String description;
}
