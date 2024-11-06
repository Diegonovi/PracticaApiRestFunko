package com.example.funko.category.controller;

import com.example.funko.category.dto.input.InputCategory;
import com.example.funko.category.dto.output.OutputCategory;
import com.example.funko.category.exceptions.CategoryDoesNotExistException;
import com.example.funko.category.mapper.CategoryMapper;
import com.example.funko.category.model.Category;
import com.example.funko.category.model.Description;
import com.example.funko.category.service.CategoryServiceImpl;
import com.example.funko.category.storage.json.CategoryJsonStorageImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private CategoryServiceImpl categoryService;

    @MockBean
    private CategoryJsonStorageImpl categoryStorage;

    @Autowired
    MockMvc mockMvc;

    @Value("/categories")
    String myEndpoint;

    @Autowired
    public CategoryControllerTest(
            CategoryServiceImpl categoryService,
            CategoryJsonStorageImpl categoryStorage
    ){
        this.categoryService = categoryService;
        this.categoryStorage = categoryStorage;
    }

    //Clases test
    Category category = new Category();
    InputCategory inputCategory = new InputCategory();
    OutputCategory outputCategory = new OutputCategory();
    Description description = new Description();

    @BeforeEach
    void setUp() {
        // Descripcion
        description.setText("test");
        // Categoria
        category.setName("category");
        category.setDescription(description);
        // Input
        inputCategory.setName(category.getName());
        inputCategory.setDescription(description.getText());
        // Output
        outputCategory.setId(category.getId().toString());
        outputCategory.setName(category.getName());
        outputCategory.setDescription(description.getText());
    }

    @Test
    void getAllCategories() throws Exception {
        // Arrange
        when(categoryService.findAll()).thenReturn(List.of(category));

        // Act & Arrange
        MockHttpServletResponse response;
        try(MockedStatic<CategoryMapper> categoryMapper = mockStatic(CategoryMapper.class)){
            categoryMapper.when(() -> CategoryMapper.toOutputCategory(category)).thenReturn(outputCategory);
            response = mockMvc.perform(
                            get(myEndpoint)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
        }
        List<OutputCategory> res = mapper.readValue(
                response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, OutputCategory.class)
        );

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals(List.of(outputCategory), res);
    }

    @Test
    void getCategoryById() throws Exception {
        // Arrange
        when(categoryService.findById(category.getId())).thenReturn(category);

        // Act & Arrange
        MockHttpServletResponse response;
        try(MockedStatic<CategoryMapper> categoryMapper = mockStatic(CategoryMapper.class)){
            categoryMapper.when(() -> CategoryMapper.toOutputCategory(category)).thenReturn(outputCategory);
            response = mockMvc.perform(
                            get(myEndpoint + "/" + category.getId())
                                   .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
        }
        OutputCategory res = mapper.readValue(response.getContentAsString(), OutputCategory.class);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals(outputCategory, res);
    }

    @Test
    void getCategoryByName() throws Exception {
        // Arrange
        when(categoryService.findByName(category.getName())).thenReturn(category);

        // Act & Arrange
        MockHttpServletResponse response;
        try(MockedStatic<CategoryMapper> categoryMapper = mockStatic(CategoryMapper.class)){
            categoryMapper.when(() -> CategoryMapper.toOutputCategory(category)).thenReturn(outputCategory);
            response = mockMvc.perform(
                            get(myEndpoint + "/name/" + category.getName())
                                   .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
        }
        OutputCategory res = mapper.readValue(response.getContentAsString(), OutputCategory.class);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals(outputCategory, res);
    }

    @Test
    void save() throws Exception {
        // Arrange
        when(categoryService.save(category)).thenReturn(category);

        // Act & Arrange
        MockHttpServletResponse response;
        try(MockedStatic<CategoryMapper> categoryMapper = mockStatic(CategoryMapper.class)){
            categoryMapper.when(() -> CategoryMapper.toCategory(inputCategory)).thenReturn(category);
            categoryMapper.when(() -> CategoryMapper.toOutputCategory(category)).thenReturn(outputCategory);
            response = mockMvc.perform(
                            post(myEndpoint)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(mapper.writeValueAsString(inputCategory))
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
        }
        OutputCategory res = mapper.readValue(response.getContentAsString(), OutputCategory.class);

        // Assert
        assertEquals(201, response.getStatus());
        assertEquals(outputCategory, res);
    }

    @Test
    void updateCategory() throws Exception {
        // Arrange
        inputCategory.setName("test");
        inputCategory.setDescription("updated description");
        when(categoryService.update(category.getId(), category)).thenReturn(category);

        // Act & Arrange
        MockHttpServletResponse response;
        try(MockedStatic<CategoryMapper> categoryMapper = mockStatic(CategoryMapper.class)){
            categoryMapper.when(() -> CategoryMapper.toCategory(inputCategory)).thenReturn(category);
            categoryMapper.when(() -> CategoryMapper.toOutputCategory(category)).thenReturn(outputCategory);
            response = mockMvc.perform(
                            put(myEndpoint + "/" + category.getId())
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .content(mapper.writeValueAsString(inputCategory))
                                   .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
        }
        OutputCategory res = mapper.readValue(response.getContentAsString(), OutputCategory.class);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals(outputCategory, res);
    }

    @Test
    void deleteCategoryPhysically() throws Exception {
        // Arrange
        category.setIsDeleted(true);
        outputCategory.setIsDeleted(true);
        when(categoryService.delete(category.getId(), true)).thenReturn(category);

        // Act
        MockHttpServletResponse response;
        try(MockedStatic<CategoryMapper> categoryMapper = mockStatic(CategoryMapper.class)) {
            categoryMapper.when(() -> CategoryMapper.toCategory(inputCategory)).thenReturn(category);
            categoryMapper.when(() -> CategoryMapper.toOutputCategory(category)).thenReturn(outputCategory);
            response = mockMvc.perform(
                            delete(myEndpoint + "/" + category.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
        }
            OutputCategory res = mapper.readValue(response.getContentAsString(), OutputCategory.class);
        // Assert
        assertEquals(200, response.getStatus());
        assertTrue(res.getIsDeleted());
    }

    @Test
    void deleteCategorySoftly() throws Exception {
        // Arrange
        category.setIsDeleted(false);
        outputCategory.setIsDeleted(false);
        when(categoryService.delete(category.getId(), false)).thenReturn(category);


        // Act
        MockHttpServletResponse response;
        try(MockedStatic<CategoryMapper> categoryMapper = mockStatic(CategoryMapper.class)) {
            categoryMapper.when(() -> CategoryMapper.toCategory(inputCategory)).thenReturn(category);
            categoryMapper.when(() -> CategoryMapper.toOutputCategory(category)).thenReturn(outputCategory);
            response = mockMvc.perform(
                            delete(myEndpoint + "/" + category.getId() + "?logically=false")
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
        }
            OutputCategory res = mapper.readValue(response.getContentAsString(), OutputCategory.class);
        // Assert
        assertEquals(200, response.getStatus());
        assertFalse(res.getIsDeleted());
    }

    @Test
    void importCategories() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "categories.json",
                "application/json",
                (" ").getBytes());
        when(categoryStorage.getCategoriesFromFile(ArgumentMatchers.any(File.class))).thenReturn(Flux.just(category));
        when(categoryService.save(category)).thenReturn(category);
        when(categoryService.findByName(category.getName())).thenThrow(new CategoryDoesNotExistException(""));

        // Act
        MockHttpServletResponse response;
        try(MockedStatic<CategoryMapper> categoryMapper = mockStatic(CategoryMapper.class)){
            categoryMapper.when(() -> CategoryMapper.toCategory(inputCategory)).thenReturn(category);
            categoryMapper.when(() -> CategoryMapper.toOutputCategory(category)).thenReturn(outputCategory);
            response = mockMvc.perform(multipart("/categories/importjson")
                            .file(mockFile))
                    .andReturn().getResponse();
        }
        List<OutputCategory> res = mapper.readValue(
                response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, OutputCategory.class)
        );
        // Assert
        assertEquals(201, response.getStatus());
        assertEquals(outputCategory, res.getFirst());
    }

}