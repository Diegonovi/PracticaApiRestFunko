package com.example.funko.funko.services;

import com.example.funko.category.exceptions.CategoryDoesNotExistException;
import com.example.funko.category.model.Category;
import com.example.funko.category.repository.CategoryRepository;
import com.example.funko.funko.exceptions.FunkoNotFoundException;
import com.example.funko.funko.model.Funko;
import com.example.funko.funko.repository.FunkosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {

    @Mock
    private FunkosRepository funkosRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private FunkoServiceImpl funkoService;

    private Funko funko;
    private Category category;

    @BeforeEach
    void setUp() {
        LocalDate releaseDate = LocalDate.parse("2022-01-01");
        category = new Category();
        category.setName("testCategory");

        funko = new Funko();
        funko.setId(1L);
        funko.setName("testFunko");
        funko.setCategory(category);
        funko.setPrice(10.0);
        funko.setReleaseDate(releaseDate);
    }

    @Test
    void findById() {
        // Arrange
        when(funkosRepository.findById(funko.getId())).thenReturn(Optional.of(funko));

        // Act
        Funko result = funkoService.findById(funko.getId());

        // Assert
        assertEquals(funko, result);
    }

    @Test
    void findByIdOfNonExistentFunko() {
        // Arrange
        when(funkosRepository.findById(funko.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                FunkoNotFoundException.class,
                () -> funkoService.findById(funko.getId())
        );
    }

    @Test
    void save() {
        // Arrange
        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));
        when(funkosRepository.save(funko)).thenReturn(funko);

        // Act
        Funko result = funkoService.save(funko);

        // Assert
        assertEquals(funko, result);
    }

    @Test
    void saveAFunkoWithANonExistingCategory() {
        // Arrange
        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                CategoryDoesNotExistException.class,
                () -> funkoService.save(funko)
        );
    }

    @Test
    void update() {
        // Arrange
        Funko updatedFunko = new Funko();
        updatedFunko.setId(funko.getId());
        updatedFunko.setName("updatedFunko");
        updatedFunko.setCategory(category);

        when(funkosRepository.findById(funko.getId())).thenReturn(Optional.of(funko));
        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));
        when(funkosRepository.save(funko)).thenReturn(updatedFunko);

        // Act
        Funko result = funkoService.update(funko.getId(), updatedFunko);

        // Assert
        assertEquals(updatedFunko.getName(), result.getName());
    }

    @Test
    void updateNonExistingFunko() {
        // Arrange
        when(funkosRepository.findById(funko.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                FunkoNotFoundException.class,
                () -> funkoService.update(funko.getId(), funko)
        );
    }

    @Test
    void delete() {
        // Arrange
        when(funkosRepository.findById(funko.getId())).thenReturn(Optional.of(funko));

        // Act
        Funko result = funkoService.delete(funko.getId());

        // Assert
        assertEquals(funko, result);
        verify(funkosRepository).deleteById(funko.getId());
    }

    @Test
    void deleteNonExistingFunko() {
        // Arrange
        when(funkosRepository.findById(funko.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                FunkoNotFoundException.class,
                () -> funkoService.delete(funko.getId())
        );
    }

    @Test
    void findByName() {
        // Arrange
        when(funkosRepository.findByName(funko.getName())).thenReturn(List.of(funko));

        // Act
        List<Funko> result = funkoService.findByName(funko.getName());

        // Assert
        assertEquals(1, result.size());
        assertEquals(funko, result.get(0));
    }

    @Test
    void findAll() {
        // Arrange
        when(funkosRepository.findAll()).thenReturn(List.of(funko));

        // Act
        List<Funko> result = funkoService.findAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(funko, result.get(0));
    }
}
