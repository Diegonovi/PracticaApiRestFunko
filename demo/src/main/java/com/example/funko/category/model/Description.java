package com.example.funko.category.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Description {
    private String text;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
