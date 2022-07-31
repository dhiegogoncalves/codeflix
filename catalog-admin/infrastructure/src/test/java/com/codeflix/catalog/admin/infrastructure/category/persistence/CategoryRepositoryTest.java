package com.codeflix.catalog.admin.infrastructure.category.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.codeflix.catalog.admin.MySQLGatewayTest;
import com.codeflix.catalog.admin.domain.category.Category;

@MySQLGatewayTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void givenAnInvalidNullName_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "name";
        final var expectedMessage = "not-null property references a null or transient value : " +
                "com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity" +
                ".name";

        final var aCategory = Category.newCategory(
                "Filme", "A categoria mais assistida", true);

        final var anEntity = CategoryJpaEntity.from(aCategory);
        anEntity.setName(null);

        final var actualException = assertThrows(
                DataIntegrityViolationException.class,
                () -> categoryRepository.save(anEntity));

        final var actualCause = assertInstanceOf(
                PropertyValueException.class, actualException.getCause());

        assertEquals(expectedPropertyName, actualCause.getPropertyName());
        assertEquals(expectedMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullCreatedAt_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "createdAt";
        final var expectedMessage = "not-null property references a null or transient value : " +
                "com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity" +
                ".createdAt";

        final var aCategory = Category.newCategory(
                "Filme", "A categoria mais assistida", true);

        final var anEntity = CategoryJpaEntity.from(aCategory);
        anEntity.setCreatedAt(null);

        final var actualException = assertThrows(
                DataIntegrityViolationException.class,
                () -> categoryRepository.save(anEntity));

        final var actualCause = assertInstanceOf(
                PropertyValueException.class, actualException.getCause());

        assertEquals(expectedPropertyName, actualCause.getPropertyName());
        assertEquals(expectedMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullUpdatedAt_whenCallsSave_shouldReturnError() {
        final var expectedPropertyName = "updatedAt";
        final var expectedMessage = "not-null property references a null or transient value : " +
                "com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity" +
                ".updatedAt";

        final var aCategory = Category.newCategory(
                "Filme", "A categoria mais assistida", true);

        final var anEntity = CategoryJpaEntity.from(aCategory);
        anEntity.setUpdatedAt(null);

        final var actualException = assertThrows(
                DataIntegrityViolationException.class,
                () -> categoryRepository.save(anEntity));

        final var actualCause = assertInstanceOf(
                PropertyValueException.class, actualException.getCause());

        assertEquals(expectedPropertyName, actualCause.getPropertyName());
        assertEquals(expectedMessage, actualCause.getMessage());
    }

}
