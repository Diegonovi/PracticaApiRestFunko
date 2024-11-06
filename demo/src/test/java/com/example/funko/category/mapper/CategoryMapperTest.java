package com.example.funko.category.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.example.funko.category.dto.input.InputCategory;
import com.example.funko.category.dto.output.OutputCategory;
import com.example.funko.category.model.Category;
import com.example.funko.category.model.Description;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

class CategoryMapperTest {

    private Category category;
    private InputCategory inputCategory;

    @BeforeEach
    void setUp() {
        Description description = new Description();
        description.setText("Test Description");

        category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Test Category");
        category.setDescription(description);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        category.setIsDeleted(false);

        // Set up for toCategory test
        inputCategory = new InputCategory();
        inputCategory.setName("Test Category");
        inputCategory.setDescription("Test Description");
    }

    @Test
    void toOutputCategory() {
        // Act
        OutputCategory result = CategoryMapper.toOutputCategory(category);

        // Assert
        assertNotNull(result);
        assertEquals(category.getId().toString(), result.getId());
        assertEquals(category.getName(), result.getName());
        assertEquals(category.getDescription().getText(), result.getDescription());
        assertEquals(category.getCreatedAt().toString(), result.getCreatedAt());
        assertEquals(category.getUpdatedAt().toString(), result.getUpdatedAt());
        assertEquals(category.getIsDeleted(), result.getIsDeleted());
    }

    @Test
    void toCategory() {
        // Act
        Category result = CategoryMapper.toCategory(inputCategory);

        // Assert
        assertNotNull(result);
        assertEquals(inputCategory.getName(), result.getName());
        assertEquals(inputCategory.getDescription(), result.getDescription().getText());
    }
}
