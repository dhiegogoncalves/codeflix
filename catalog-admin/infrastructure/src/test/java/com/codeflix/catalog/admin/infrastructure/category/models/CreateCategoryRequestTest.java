package com.codeflix.catalog.admin.infrastructure.category.models;

import java.io.IOException;
import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.codeflix.catalog.admin.JacksonTest;

@JacksonTest
public class CreateCategoryRequestTest {

    @Autowired
    private JacksonTester<CreateCategoryRequest> json;

    @Test
    public void testMarshall() throws IOException {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = false;

        final var response = new CreateCategoryRequest(
                expectedName,
                expectedDescription,
                expectedIsActive);

        final var actualjson = this.json.write(response);

        Assertions.assertThat(actualjson)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.description", expectedDescription)
                .hasJsonPathValue("$.is_active", expectedIsActive);
    }

    @Test
    public void testUnmarshall() throws IOException {
        final var expectedName = "Filmes";
        final var expectedDescription = "A categoria mais assistida";
        final var expectedIsActive = true;

        final var json = """
                {
                    "name":"%s",
                    "description":"%s",
                    "is_active": %s
                }
                """.formatted(
                expectedName,
                expectedDescription,
                expectedIsActive);

        final var actualjson = this.json.parse(json);

        Assertions.assertThat(actualjson)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("description", expectedDescription)
                .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }
}
