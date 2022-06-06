package com.codeflix.catalog.admin.application.category.delete;

import java.util.Objects;

import com.codeflix.catalog.admin.domain.category.CategoryGateway;
import com.codeflix.catalog.admin.domain.category.CategoryID;

public class DefaultDeleteCategoryUseCase extends UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultDeleteCategoryUseCase(final CategoryGateway categoryGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
    }

    @Override
    public void execute(final String anId) {
        this.categoryGateway.deleteById(CategoryID.from(anId));
    }
}
