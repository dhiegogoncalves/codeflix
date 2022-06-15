package com.codeflix.catalog.admin.application.category.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codeflix.catalog.admin.domain.category.Category;
import com.codeflix.catalog.admin.domain.category.CategoryGateway;
import com.codeflix.catalog.admin.domain.category.CategoryID;
import com.codeflix.catalog.admin.domain.exceptions.DomainException;

@ExtendWith(MockitoExtension.class)
class UpdateCategoryUseCaseTest {

    @InjectMocks
    DefaultUpdateCategoryUseCase defaultUpdateCategoryUseCase;

    @Mock
    CategoryGateway categoryGateway;

    @Captor
    ArgumentCaptor<Category> categoryCaptor;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_thenShouldReturnCategoryId() {
        final var aCategory = Category.newCategory(
                "Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(expectedId))
                .thenReturn(Optional.of(Category.with(aCategory)));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = defaultUpdateCategoryUseCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).findById(expectedId);

        verify(categoryGateway, times(1))
                .update(categoryCaptor.capture());

        final var anUpdateCategory = categoryCaptor.getValue();

        assertEquals(expectedName, anUpdateCategory.getName());
        assertEquals(expectedDescription, anUpdateCategory.getDescription());
        assertEquals(expectedIsActive, anUpdateCategory.isActive());
        assertEquals(expectedId, anUpdateCategory.getId());
        assertTrue(aCategory.getUpdatedAt().isBefore(anUpdateCategory.getUpdatedAt()));
        assertNull(aCategory.getDeletedAt());
    }

    @Test
    void givenAInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        final var aCategory = Category.newCategory(
                "Film", null, true);

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(expectedId))
                .thenReturn(Optional.of(Category.with(aCategory)));

        final var notification = defaultUpdateCategoryUseCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    void givenAValidInactiveCommand_whenCallsUpdateCategory_thenShouldReturnInactiveCategoryId() {
        final var aCategory = Category.newCategory(
                "Film", null, true);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(expectedId))
                .thenReturn(Optional.of(Category.with(aCategory)));
        when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

        assertTrue(aCategory.isActive());
        assertNull(aCategory.getDeletedAt());

        final var actualOutput = defaultUpdateCategoryUseCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).findById(expectedId);

        verify(categoryGateway, times(1))
                .update(categoryCaptor.capture());

        final var anUpdateCategory = categoryCaptor.getValue();

        assertEquals(expectedName, anUpdateCategory.getName());
        assertEquals(expectedDescription, anUpdateCategory.getDescription());
        assertEquals(expectedIsActive, anUpdateCategory.isActive());
        assertEquals(expectedId, anUpdateCategory.getId());
        assertTrue(aCategory.getUpdatedAt().isBefore(anUpdateCategory.getUpdatedAt()));
        assertNotNull(anUpdateCategory.getDeletedAt());
    }

    @Test
    void givenAInvalidCommand_whenGatewayThrowsRandomException_thenShouldReturnAException() {
        final var aCategory = Category.newCategory(
                "Film", null, true);

        final String expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Gateway error";
        final var expectedErrorCount = 1;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(expectedId))
                .thenReturn(Optional.of(Category.with(aCategory)));
        when(categoryGateway.update(any())).thenThrow(new IllegalStateException("Gateway error"));

        final var notification = defaultUpdateCategoryUseCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(1))
                .update(categoryCaptor.capture());

        final var anUpdateCategory = categoryCaptor.getValue();

        assertEquals(expectedName, anUpdateCategory.getName());
        assertEquals(expectedDescription, anUpdateCategory.getDescription());
        assertEquals(expectedIsActive, anUpdateCategory.isActive());
        assertEquals(expectedId, anUpdateCategory.getId());
        assertTrue(aCategory.getUpdatedAt().isBefore(anUpdateCategory.getUpdatedAt()));
        assertNull(anUpdateCategory.getDeletedAt());
    }

    @Test
    void givenACommandWithInvalidID_whenCallsUpdateCategory_thenShouldReturnNotFoundException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = "123";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "Category with ID 123 was not found";

        final var aCommand = UpdateCategoryCommand.with(
                expectedId, expectedName, expectedDescription, expectedIsActive);

        when(categoryGateway.findById(CategoryID.from(expectedId)))
                .thenReturn(Optional.empty());

        final var actualException = assertThrows(
                DomainException.class,
                () -> defaultUpdateCategoryUseCase.execute(aCommand));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

        verify(categoryGateway, times(1))
                .findById(CategoryID.from(expectedId));

        verify(categoryGateway, times(0)).update(any());
    }
}
