package com.example.funko.funko.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FunkoWithCategoryName {
    private Long id = 0L;
    private String name;
    private Double price;
    private String category;
    private String releaseDate;
}
