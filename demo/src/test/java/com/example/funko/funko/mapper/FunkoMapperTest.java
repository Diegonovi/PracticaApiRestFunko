package com.example.funko.funko.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.example.funko.category.model.Category;
import com.example.funko.funko.dto.input.InputFunko;
import com.example.funko.funko.dto.output.OutputFunko;
import com.example.funko.funko.model.Funko;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FunkoMapperTest {

    private InputFunko inputFunko;
    private Funko funko;
    private Category category;

    @BeforeEach
    void setUp() {
        inputFunko = new InputFunko();
        inputFunko.setName("Test Funko");
        inputFunko.setPrice(19.99);
        inputFunko.setReleaseDate(LocalDate.parse("2023-10-10"));
        inputFunko.setCategory("Test Category");

        category = Category.builder().name("Test Category").build();
        funko = new Funko();
        funko.setId(1L);
        funko.setName("Test Funko");
        funko.setPrice(19.99);
        funko.setReleaseDate(LocalDate.parse("2023-10-10"));
        funko.setCategory(category);
        funko.setCreatedAt(java.time.LocalDateTime.now());
        funko.setUpdatedAt(java.time.LocalDateTime.now());
    }

    @Test
    void toFunko() {
        // Act
        Funko result = FunkoMapper.toFunko(inputFunko);

        // Assert
        assertNotNull(result);
        assertEquals(inputFunko.getName(), result.getName());
        assertEquals(inputFunko.getPrice(), result.getPrice());
        assertEquals(inputFunko.getReleaseDate(), result.getReleaseDate());
        assertEquals(inputFunko.getCategory(), result.getCategory().getName());
    }

    @Test
    void toOutputFunko() {
        // Act
        OutputFunko result = FunkoMapper.toOutputFunko(funko);

        // Assert
        assertNotNull(result);
        assertEquals(funko.getId(), result.getId());
        assertEquals(funko.getName(), result.getName());
        assertEquals(funko.getReleaseDate(), result.getReleaseDate());
        assertEquals(funko.getCategory().getName(), result.getCategory());
        assertEquals(funko.getPrice(), result.getPrice());
        assertEquals(funko.getCreatedAt().toString(), result.getCreatedAt());
        assertEquals(funko.getUpdatedAt().toString(), result.getUpdatedAt());
    }
}
