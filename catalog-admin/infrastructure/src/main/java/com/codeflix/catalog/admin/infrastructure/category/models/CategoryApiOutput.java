package com.codeflix.catalog.admin.infrastructure.category.models;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryApiOutput(
        String id,
        String name,
        String description,
        @JsonProperty("is_active") Boolean isActive,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("deleted_at") Instant deletedAt) {

}
