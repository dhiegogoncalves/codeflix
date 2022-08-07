package com.codeflix.catalog.admin.infrastructure.api.controllers;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.codeflix.catalog.admin.application.category.create.CreateCategoryCommand;
import com.codeflix.catalog.admin.application.category.create.CreateCategoryOutput;
import com.codeflix.catalog.admin.application.category.create.CreateCategoryUseCase;
import com.codeflix.catalog.admin.domain.pagination.Pagination;
import com.codeflix.catalog.admin.domain.validation.handler.Notification;
import com.codeflix.catalog.admin.infrastructure.api.CategoryAPI;
import com.codeflix.catalog.admin.infrastructure.category.models.CreateCategoryApiInput;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;

    public CategoryController(final CreateCategoryUseCase createCategoryUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory(CreateCategoryApiInput input) {
        final var aCommand = CreateCategoryCommand.with(
                input.name(),
                input.description(),
                input.isActive() != null ? input.isActive() : true);

        final Function<Notification, ResponseEntity<?>> onError = notification -> ResponseEntity
                .unprocessableEntity().body(notification);

        final Function<CreateCategoryOutput, ResponseEntity<?>> onSuccess = output -> ResponseEntity
                .created(URI.create("/categories/" + output.id())).body(output);

        return this.createCategoryUseCase.execute(aCommand).fold(onError, onSuccess);
    }

    @Override
    public Pagination<?> listCategories(String search, int page, int perPage, String sort, String direction) {
        return null;
    }

}
