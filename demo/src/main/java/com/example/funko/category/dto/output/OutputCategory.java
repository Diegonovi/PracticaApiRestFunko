package com.example.funko.category.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputCategory {
    private String id;
    private String name;
    private String createdAt;
    private String updatedAt;
    private Boolean isDeleted = false;
}

