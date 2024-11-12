package com.example.funko.funko.controller;

import com.example.funko.category.model.Category;
import com.example.funko.funko.dto.input.InputFunko;
import com.example.funko.funko.dto.output.OutputFunko;
import com.example.funko.funko.mapper.FunkoMapper;
import com.example.funko.funko.model.Funko;
import com.example.funko.funko.services.FunkoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class FunkoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FunkoService service;

    private final ObjectMapper mapper = new ObjectMapper();

    private Funko funko;
    private InputFunko inputFunko;
    private OutputFunko outputFunko;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());

        LocalDate releaseDate = LocalDate.parse("2022-01-01");
        Category category = new Category();
        funko = new Funko();
        inputFunko = new InputFunko();
        outputFunko = new OutputFunko();

        category.setName("Idk");

        //Funko
        funko.setId(1L);
        funko.setName("Test Funko");
        funko.setPrice(10.0);
        funko.setReleaseDate(releaseDate);
        funko.setCategory(category);

        //InputFunko
        inputFunko.setName(funko.getName());
        inputFunko.setPrice(funko.getPrice());
        inputFunko.setReleaseDate(funko.getReleaseDate());
        inputFunko.setCategory(category.getName());

        //OutputFunko
        outputFunko.setId(funko.getId());
        outputFunko.setName(funko.getName());
        outputFunko.setPrice(funko.getPrice());
        outputFunko.setReleaseDate(funko.getReleaseDate());
        outputFunko.setCategory(category.getName());
        outputFunko.setCreatedAt(category.getCreatedAt().toString());
        outputFunko.setUpdatedAt(category.getUpdatedAt().toString());
    }

    @Test
    void getAllFunkos() throws Exception {
        when(service.findAll()).thenReturn(List.of(funko));

        try (MockedStatic<FunkoMapper> mapperMock = mockStatic(FunkoMapper.class)) {
            mapperMock.when(() -> FunkoMapper.toOutputFunko(funko)).thenReturn(outputFunko);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/funkos")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            List<OutputFunko> result = mapper.readValue(
                    response.getContentAsString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, OutputFunko.class)
            );

            assertEquals(200, response.getStatus());
            assertEquals(List.of(outputFunko), result);
        }
    }

    @Test
    void getFunkoById() throws Exception {
        when(service.findById(funko.getId())).thenReturn(funko);

        try (MockedStatic<FunkoMapper> mapperMock = mockStatic(FunkoMapper.class)) {
            mapperMock.when(() -> FunkoMapper.toOutputFunko(funko)).thenReturn(outputFunko);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/funkos/" + funko.getId())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            OutputFunko result = mapper.readValue(response.getContentAsString(), OutputFunko.class);

            assertEquals(200, response.getStatus());
            assertEquals(outputFunko, result);
        }
    }

    @Test
    void getFunkosByNombre() throws Exception {
        String name = "Test Funko";
        when(service.findByName(name)).thenReturn(List.of(funko));

        try (MockedStatic<FunkoMapper> mapperMock = mockStatic(FunkoMapper.class)) {
            mapperMock.when(() -> FunkoMapper.toOutputFunko(funko)).thenReturn(outputFunko);
            MockHttpServletResponse response = mockMvc.perform(
                            get("/funkos/name/" + name)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            List<OutputFunko> result = mapper.readValue(
                    response.getContentAsString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, OutputFunko.class)
            );

            assertEquals(200, response.getStatus());
            assertEquals(List.of(outputFunko), result);
        }
    }

    @Test
    void save() throws Exception {
        // Arrange
        when(service.save(funko)).thenReturn(funko);

        // Act
        try (MockedStatic<FunkoMapper> mapperMock = mockStatic(FunkoMapper.class)) {
            mapperMock.when(() -> FunkoMapper.toFunkoWithProvisionalCategory(inputFunko)).thenReturn(funko);
            mapperMock.when(() -> FunkoMapper.toOutputFunko(funko)).thenReturn(outputFunko);
            MockHttpServletResponse response = mockMvc.perform(
                            post("/funkos")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(mapper.writeValueAsString(inputFunko))
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            OutputFunko result = mapper.readValue(response.getContentAsString(), OutputFunko.class);

            // Assert
            assertEquals(201, response.getStatus());
            assertEquals(outputFunko, result);
        }
    }

    @Test
    void saveInvalidFunko() throws Exception{

    }

    @Test
    void updateFunko() throws Exception {
        inputFunko.setName("Updated Funko");
        funko.setName("Updated Funko");
        when(service.update(funko.getId(), funko)).thenReturn(funko);

        try (MockedStatic<FunkoMapper> mapperMock = mockStatic(FunkoMapper.class)) {
            mapperMock.when(() -> FunkoMapper.toFunkoWithProvisionalCategory(inputFunko)).thenReturn(funko);
            mapperMock.when(() -> FunkoMapper.toOutputFunko(funko)).thenReturn(outputFunko);
            MockHttpServletResponse response = mockMvc.perform(
                            put("/funkos/" + funko.getId())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(mapper.writeValueAsString(inputFunko))
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            OutputFunko result = mapper.readValue(response.getContentAsString(), OutputFunko.class);

            assertEquals(200, response.getStatus());
            assertEquals(outputFunko, result);
        }
    }

    @Test
    void deleteFunko() throws Exception {
        when(service.delete(funko.getId())).thenReturn(funko);

        try (MockedStatic<FunkoMapper> mapperMock = mockStatic(FunkoMapper.class)) {
            mapperMock.when(() -> FunkoMapper.toOutputFunko(funko)).thenReturn(outputFunko);
            MockHttpServletResponse response = mockMvc.perform(
                            delete("/funkos/" + funko.getId())
                                    .accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();

            OutputFunko result = mapper.readValue(response.getContentAsString(), OutputFunko.class);

            assertEquals(200, response.getStatus());
            assertEquals(outputFunko, result);
        }
    }
}
