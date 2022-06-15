package com.codeflix.catalog.admin.application.category.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeflix.catalog.admin.domain.category.Category;
import com.codeflix.catalog.admin.domain.category.CategoryGateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @InjectMocks
    DefaultCreateCategoryUseCase defaultCreateCategoryUseCase;

    @Mock
    CategoryGateway categoryGateway;

    @Captor
    ArgumentCaptor<Category> categoryCaptor;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    void givenAValidCommand_whenCallsCreateCategory_thenShouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var aCommand = CreateCategoryCommand.with(
                expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.create(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = defaultCreateCategoryUseCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1))
                .create(categoryCaptor.capture());

        final var aCategory = categoryCaptor.getValue();

        assertEquals(expectedName, aCategory.getName());
        assertEquals(expectedDescription, aCategory.getDescription());
        assertEquals(expectedIsActive, aCategory.isActive());
        assertNotNull(aCategory.getId());
        assertNotNull(aCategory.getCreatedAt());
        assertNotNull(aCategory.getUpdatedAt());
        assertNull(aCategory.getDeletedAt());
    }

    @Test
    void givenAInvalidName_whenCallsCreateCategory_thenShouldReturnDomainException() {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        final var aCommand = CreateCategoryCommand.with(
                expectedName, expectedDescription, expectedIsActive);

        final var notification = defaultCreateCategoryUseCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    void givenAValidCommandWithInactiveCategory_whenCallsCreateCategory_thenShouldReturnInactiveCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var aCommand = CreateCategoryCommand.with(
                expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.create(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = defaultCreateCategoryUseCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1))
                .create(categoryCaptor.capture());

        final var aCategory = categoryCaptor.getValue();

        assertEquals(expectedName, aCategory.getName());
        assertEquals(expectedDescription, aCategory.getDescription());
        assertEquals(expectedIsActive, aCategory.isActive());
        assertNotNull(aCategory.getId());
        assertNotNull(aCategory.getCreatedAt());
        assertNotNull(aCategory.getUpdatedAt());
        assertNotNull(aCategory.getDeletedAt());
    }

    @Test
    void givenAValidCommand_whenGatewayThrowsRandomException_thenShouldReturnAException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Gateway error";
        final var expectedErrorCount = 1;

        final var aCommand = CreateCategoryCommand.with(
                expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.create(any()))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var notification = defaultCreateCategoryUseCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(1))
                .create(categoryCaptor.capture());

        final var aCategory = categoryCaptor.getValue();

        assertEquals(expectedName, aCategory.getName());
        assertEquals(expectedDescription, aCategory.getDescription());
        assertEquals(expectedIsActive, aCategory.isActive());
        assertNotNull(aCategory.getId());
        assertNotNull(aCategory.getCreatedAt());
        assertNotNull(aCategory.getUpdatedAt());
        assertNull(aCategory.getDeletedAt());
    }
}
