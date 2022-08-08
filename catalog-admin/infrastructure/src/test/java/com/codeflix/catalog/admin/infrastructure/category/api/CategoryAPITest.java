package com.codeflix.catalog.admin.infrastructure.category.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.codeflix.catalog.admin.ControllerTest;
import com.codeflix.catalog.admin.application.category.create.CreateCategoryCommand;
import com.codeflix.catalog.admin.application.category.create.CreateCategoryOutput;
import com.codeflix.catalog.admin.application.category.create.CreateCategoryUseCase;
import com.codeflix.catalog.admin.domain.exceptions.DomainException;
import com.codeflix.catalog.admin.domain.validation.handler.Notification;
import com.codeflix.catalog.admin.domain.validation.Error;
import com.codeflix.catalog.admin.infrastructure.api.CategoryAPI;
import com.codeflix.catalog.admin.infrastructure.category.models.CreateCategoryApiInput;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vavr.control.Either;

@ControllerTest(controllers = CategoryAPI.class)
public class CategoryAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Captor
    private ArgumentCaptor<CreateCategoryCommand> categoryCaptor;

    @MockBean
    private CreateCategoryUseCase createCategoryUseCase;

    @Test
    void givenAValidCommand_whenCallsCreateCategory_thenShouldReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var anInput = new CreateCategoryApiInput(
                expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenReturn(Either.right(CreateCategoryOutput.from("123")));

        final var request = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/categories/123"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", Matchers.equalTo("123")));

        verify(createCategoryUseCase).execute(categoryCaptor.capture());

        final var createCategoryCommand = categoryCaptor.getValue();

        assertEquals(expectedName, createCategoryCommand.name());
        assertEquals(expectedDescription, createCategoryCommand.description());
        assertEquals(expectedIsActive, createCategoryCommand.isActive());
    }

    @Test
    void givenAnInvalidName_whenCallsCreateCategory_thenShouldReturnNotification() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedMessage = "'name' should not be null";

        final var anInput = new CreateCategoryApiInput(
                expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenReturn(Either.left(
                        Notification.create(new Error(expectedMessage))));

        final var request = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", Matchers.nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", Matchers.equalTo(expectedMessage)));

        verify(createCategoryUseCase).execute(categoryCaptor.capture());

        final var createCategoryCommand = categoryCaptor.getValue();

        assertEquals(expectedName, createCategoryCommand.name());
        assertEquals(expectedDescription, createCategoryCommand.description());
        assertEquals(expectedIsActive, createCategoryCommand.isActive());
    }

    @Test
    void givenAnInvalidCommand_whenCallsCreateCategory_thenShouldReturnDomainException() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedMessage = "'name' should not be null";

        final var anInput = new CreateCategoryApiInput(
                expectedName, expectedDescription, expectedIsActive);

        when(createCategoryUseCase.execute(any()))
                .thenThrow(DomainException.with(new Error(expectedMessage)));

        final var request = MockMvcRequestBuilders.post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(anInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Location", Matchers.nullValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", Matchers.equalTo(expectedMessage)));

        verify(createCategoryUseCase).execute(categoryCaptor.capture());

        final var createCategoryCommand = categoryCaptor.getValue();

        assertEquals(expectedName, createCategoryCommand.name());
        assertEquals(expectedDescription, createCategoryCommand.description());
        assertEquals(expectedIsActive, createCategoryCommand.isActive());
    }

}
