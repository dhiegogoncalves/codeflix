package com.codeflix.catalog.admin.application.category.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.codeflix.catalog.admin.IntegrationTest;
import com.codeflix.catalog.admin.domain.category.CategoryGateway;
import com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryRepository;

@IntegrationTest
class CreateCategoryUseCaseIT {

    @Autowired
    private DefaultCreateCategoryUseCase defaultCreateCategoryUseCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallsCreateCategory_thenShouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        assertEquals(0, categoryRepository.count());

        final var aCommand = CreateCategoryCommand.with(
                expectedName, expectedDescription, expectedIsActive);

        final var actualOutput = defaultCreateCategoryUseCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());
        assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryRepository.findById(actualOutput.id().getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertNotNull(actualCategory.getCreatedAt());
        assertNotNull(actualCategory.getUpdatedAt());
        assertNull(actualCategory.getDeletedAt());
    }

    @Test
    void givenAInvalidName_whenCallsCreateCategory_thenShouldReturnDomainException() {
        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;

        assertEquals(0, categoryRepository.count());

        final var aCommand = CreateCategoryCommand.with(
                expectedName, expectedDescription, expectedIsActive);

        final var notification = defaultCreateCategoryUseCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        assertEquals(0, categoryRepository.count());

        verify(categoryGateway, times(0)).create(any());
    }

    @Test
    void givenAValidCommandWithInactiveCategory_whenCallsCreateCategory_thenShouldReturnInactiveCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        assertEquals(0, categoryRepository.count());

        final var aCommand = CreateCategoryCommand.with(
                expectedName, expectedDescription, expectedIsActive);

        final var actualOutput = defaultCreateCategoryUseCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());
        assertEquals(1, categoryRepository.count());

        final var actualCategory = categoryRepository.findById(actualOutput.id().getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertNotNull(actualCategory.getCreatedAt());
        assertNotNull(actualCategory.getUpdatedAt());
        assertNotNull(actualCategory.getDeletedAt());
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

        doThrow(new IllegalStateException(expectedErrorMessage)).when(categoryGateway).create(any());

        final var notification = defaultCreateCategoryUseCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());
    }
}
