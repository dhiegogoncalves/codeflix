package com.codeflix.catalog.admin.application.category.delete;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codeflix.catalog.admin.domain.category.Category;
import com.codeflix.catalog.admin.domain.category.CategoryGateway;
import com.codeflix.catalog.admin.domain.category.CategoryID;

@ExtendWith(MockitoExtension.class)
public class DeleteCategoryUseCaseTest {

    @InjectMocks
    DefaultDeleteCategoryUseCase defaultDeleteCategoryUseCase;

    @Mock
    CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    void givenAValidId_whenCallsDeleteCategory_thenShouldBeOK() {
        final var aCategory = Category.newCategory(
                "Film", null, true);
        final var expectedId = aCategory.getId();

        doNothing().when(categoryGateway).deleteById(expectedId);

        assertDoesNotThrow(() -> defaultDeleteCategoryUseCase.execute(expectedId.getValue()));

        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAInvalidId_whenCallsDeleteCategory_thenShouldBeOK() {
        final var expectedId = CategoryID.from("123");

        doNothing().when(categoryGateway).deleteById(expectedId);

        assertDoesNotThrow(() -> defaultDeleteCategoryUseCase.execute(expectedId.getValue()));

        verify(categoryGateway, times(1)).deleteById(expectedId);
    }

    @Test
    void givenAValidId_whenGatewayThrowsException_thenShouldReturnException() {
        final var aCategory = Category.newCategory(
                "Film", null, true);
        final var expectedId = aCategory.getId();

        doThrow(new IllegalStateException("Gateway error"))
                .when(categoryGateway).deleteById(expectedId);

        assertThrows(
                IllegalStateException.class,
                () -> defaultDeleteCategoryUseCase.execute(expectedId.getValue()));

        verify(categoryGateway, times(1)).deleteById(expectedId);
    }
}
