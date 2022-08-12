package com.codeflix.catalog.admin.application.category.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.codeflix.catalog.admin.IntegrationTest;
import com.codeflix.catalog.admin.domain.category.Category;
import com.codeflix.catalog.admin.domain.category.CategoryGateway;
import com.codeflix.catalog.admin.domain.exceptions.DomainException;
import com.codeflix.catalog.admin.domain.exceptions.NotFoundException;
import com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryRepository;

@IntegrationTest
class UpdateCategoryUseCaseIT {

    @Autowired
    private DefaultUpdateCategoryUseCase defaultUpdateCategoryUseCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommand_whenCallsUpdateCategory_thenShouldReturnCategoryId() {
        final var aCategory = Category.newCategory(
                "Film", null, true);

        save(aCategory);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        assertEquals(1, categoryRepository.count());

        final var actualOutput = defaultUpdateCategoryUseCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        final var actualCategory = categoryRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                actualCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertTrue(aCategory.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)
                .isBefore(actualCategory.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)));
        assertNull(actualCategory.getDeletedAt());
    }

    @Test
    void givenAInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        final var aCategory = Category.newCategory(
                "Film", null, true);

        save(aCategory);

        final String expectedName = null;
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        final var notification = defaultUpdateCategoryUseCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        verify(categoryGateway, times(0)).update(any());
    }

    @Test
    void givenAValidInactiveCommand_whenCallsUpdateCategory_thenShouldReturnInactiveCategoryId() {
        final var aCategory = Category.newCategory(
                "Film", null, true);

        save(aCategory);

        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        assertTrue(aCategory.isActive());
        assertNull(aCategory.getDeletedAt());

        final var actualOutput = defaultUpdateCategoryUseCase.execute(aCommand).get();

        assertNotNull(actualOutput);
        assertNotNull(actualOutput.id());

        final var actualCategory = categoryRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedName, actualCategory.getName());
        assertEquals(expectedDescription, actualCategory.getDescription());
        assertEquals(expectedIsActive, actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                actualCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertTrue(aCategory.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)
                .isBefore(actualCategory.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS)));
        assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    void givenAInvalidCommand_whenGatewayThrowsRandomException_thenShouldReturnAException() {
        final var aCategory = Category.newCategory(
                "Film", null, true);

        save(aCategory);

        final String expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "Gateway error";
        final var expectedErrorCount = 1;
        final var expectedId = aCategory.getId();

        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(), expectedName, expectedDescription, expectedIsActive);

        doThrow(new IllegalStateException("Gateway error")).when(categoryGateway).update(any());

        final var notification = defaultUpdateCategoryUseCase.execute(aCommand).getLeft();

        assertEquals(expectedErrorCount, notification.getErrors().size());
        assertEquals(expectedErrorMessage, notification.firstError().message());

        final var actualCategory = categoryRepository.findById(expectedId.getValue()).get();

        assertEquals(aCategory.getName(), actualCategory.getName());
        assertEquals(aCategory.getDescription(), actualCategory.getDescription());
        assertEquals(aCategory.isActive(), actualCategory.isActive());
        assertEquals(aCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS),
                actualCategory.getCreatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(aCategory.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS),
                actualCategory.getUpdatedAt().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
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

        final var actualException = assertThrows(
                NotFoundException.class,
                () -> defaultUpdateCategoryUseCase.execute(aCommand));

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    private void save(final Category... aCategory) {
        categoryRepository.saveAllAndFlush(
                Arrays.stream(aCategory)
                        .map(CategoryJpaEntity::from)
                        .toList());
    }
}
