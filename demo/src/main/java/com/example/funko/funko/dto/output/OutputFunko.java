package com.example.funko.funko.dto.output;

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
public class OutputFunko {
    private Long id = 0L;
    private String name;
    private Double price;
    private LocalDate releaseDate;
    private String category;
    private String createdAt;
    private String updatedAt;
}

