package com.example.funko.category.service;

import com.example.funko.category.exceptions.CategoryAlreadyExistsException;
import com.example.funko.category.exceptions.CategoryDoesNotExistException;
import com.example.funko.category.model.Category;
import com.example.funko.category.model.Description;
import com.example.funko.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        Description description = new Description();
        description.setText("description");
        category.setName("test");
        category.setDescription(description);
    }

    @Test
    void findById() {
        // Arrange
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.findById(category.getId());

        // Assert
        assertEquals(category, result);
    }

    @Test
    void findByIdOnACategoryThatDoesntExist() {
        // Arrange
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                CategoryDoesNotExistException.class,
                () -> categoryService.findById(category.getId())
        );
    }

    @Test
    void save() {
        // Arrange
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        Category result = categoryService.save(category);

        // Assert
        assertEquals(category, result);
    }

    @Test
    void saveAnExistingCategory() {
        // Arrange
        when(categoryRepository.findByName(category.getName())).thenThrow(new CategoryAlreadyExistsException(""));

        // Act & Assert
        assertThrows(
                CategoryAlreadyExistsException.class,
                () -> categoryService.save(category)
        );
    }

    @Test
    void update() {
        // Arrange
        Category updatedCategory = category;
        updatedCategory.setName("updated");
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(updatedCategory);

        // Act
        Category result = categoryService.update(category.getId(),updatedCategory);

        // Assert
        assertEquals(category, result);
    }

    @Test
    void updateOnACategoryThatDoesntExist() {
        // Arrange
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                CategoryDoesNotExistException.class,
                () -> categoryService.update(category.getId(), category)
        );
    }

    @Test
    void deletePhysically() {
        // Arrange
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.delete(category.getId(), false);

        // Assert
        assertTrue(result.getIsDeleted());
    }

    @Test
    void findByName() {
        // Arrange
        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.findByName(category.getName());

        // Assert
        assertEquals(category, result);
    }

    @Test
    void findAll() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(category, result.getFirst());
    }
}