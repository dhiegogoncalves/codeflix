package com.codeflix.catalog.admin.infrastructure.category.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateCategoryApiInput(
        String name,
        String description,
        @JsonProperty("is_active") Boolean isActive) {
}
