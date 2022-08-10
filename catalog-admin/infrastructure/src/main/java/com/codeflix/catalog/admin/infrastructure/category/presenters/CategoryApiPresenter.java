package com.codeflix.catalog.admin.infrastructure.category.presenters;

import com.codeflix.catalog.admin.application.category.retrieve.get.CategoryOutput;
import com.codeflix.catalog.admin.infrastructure.category.models.CategoryApiOutput;

public interface CategoryApiPresenter {

    /*
     * Function<CategoryOutput, CategoryApiOutput> present = output -> new
     * CategoryApiOutput(
     * output.id().getValue(),
     * output.name(),
     * output.description(),
     * output.isActive(),
     * output.createdAt(),
     * output.updatedAt(),
     * output.deletedAt());
     */

    static CategoryApiOutput present(final CategoryOutput output) {
        return new CategoryApiOutput(
                output.id().getValue(),
                output.name(),
                output.description(),
                output.isActive(),
                output.createdAt(),
                output.updatedAt(),
                output.deletedAt());
    }
}
