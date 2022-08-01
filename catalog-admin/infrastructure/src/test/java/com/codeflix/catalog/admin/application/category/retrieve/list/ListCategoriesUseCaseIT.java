package com.codeflix.catalog.admin.application.category.retrieve.list;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.codeflix.catalog.admin.IntegrationTest;
import com.codeflix.catalog.admin.domain.category.Category;
import com.codeflix.catalog.admin.domain.category.CategoryGateway;
import com.codeflix.catalog.admin.domain.category.CategorySearchQuery;
import com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryJpaEntity;
import com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryRepository;

@IntegrationTest
class ListCategoriesUseCaseIT {

    @Autowired
    private DefaultListCategoriesUseCase defaultListCategoriesUseCase;

    @Autowired
    private CategoryRepository categoryRepository;

    @SpyBean
    private CategoryGateway categoryGateway;

    @BeforeEach
    void mockUp() {
        final var categories = Stream.of(
                Category.newCategory("Filmes", null, true),
                Category.newCategory("Netflix Originals", "Títulos de autoria da Netflix", true),
                Category.newCategory("Amazon Originals", "Títulos de autoria da Amazon Prime", true),
                Category.newCategory("Documentários", null, true),
                Category.newCategory("Sports", null, true),
                Category.newCategory("Kids", "Categoria para crianças", true),
                Category.newCategory("Series", null, true))
                .map(CategoryJpaEntity::from)
                .toList();

        categoryRepository.saveAllAndFlush(categories);
    }

    @Test
    void givenAValidTerm_whenTermDoesntMatchsPrePersisted_thenShouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "asdfgfd";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var expectedTotal = 0;

        final var aQuery = new CategorySearchQuery(
                expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedResult = defaultListCategoriesUseCase.execute(aQuery);

        assertEquals(expectedItemsCount, expectedResult.items().size());
        assertEquals(expectedPage, expectedResult.currentPage());
        assertEquals(expectedPerPage, expectedResult.perPage());
        assertEquals(expectedTotal, expectedResult.total());
    }

    @ParameterizedTest
    @CsvSource({
            "fil,0,10,1,1,Filmes",
            "net,0,10,1,1,Netflix Originals",
            "ZON,0,10,1,1,Amazon Originals",
            "KI,0,10,1,1,Kids",
            "crianças,0,10,1,1,Kids",
            "da Amazon,0,10,1,1,Amazon Originals",
    })
    void givenAValidTerm_whenCallsListCategories_thenShouldReturnCategoriesFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new CategorySearchQuery(
                expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedResult = defaultListCategoriesUseCase.execute(aQuery);

        assertEquals(expectedItemsCount, expectedResult.items().size());
        assertEquals(expectedPage, expectedResult.currentPage());
        assertEquals(expectedPerPage, expectedResult.perPage());
        assertEquals(expectedTotal, expectedResult.total());
        assertEquals(expectedCategoryName, expectedResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,7,7,Amazon Originals",
            "name,desc,0,10,7,7,Sports",
            "createdAt,asc,0,10,7,7,Filmes",
            "createdAt,desc,0,10,7,7,Series",
    })
    void givenAValidSortAndDirection_whenCallsListCategories_thenShouldReturnCategoriesOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoryName) {
        final var expectedTerms = "";

        final var aQuery = new CategorySearchQuery(
                expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedResult = defaultListCategoriesUseCase.execute(aQuery);

        assertEquals(expectedItemsCount, expectedResult.items().size());
        assertEquals(expectedPage, expectedResult.currentPage());
        assertEquals(expectedPerPage, expectedResult.perPage());
        assertEquals(expectedTotal, expectedResult.total());
        assertEquals(expectedCategoryName, expectedResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,7,Amazon Originals;Documentários",
            "1,2,2,7,Filmes;Kids",
            "2,2,2,7,Netflix Originals;Series",
            "3,2,1,7,Sports",
    })
    void givenAValidPage_whenCallsListCategories_shouldReturnCategoriesPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedCategoriesName) {
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new CategorySearchQuery(
                expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var expectedResult = defaultListCategoriesUseCase.execute(aQuery);

        assertEquals(expectedItemsCount, expectedResult.items().size());
        assertEquals(expectedPage, expectedResult.currentPage());
        assertEquals(expectedPerPage, expectedResult.perPage());
        assertEquals(expectedTotal, expectedResult.total());

        var index = 0;
        for (final var expectedName : expectedCategoriesName.split(";")) {
            final var actualName = expectedResult.items().get(index).name();
            assertEquals(expectedName, actualName);
            index++;
        }
    }
}
