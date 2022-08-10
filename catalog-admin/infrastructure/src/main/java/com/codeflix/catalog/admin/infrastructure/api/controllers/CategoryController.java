package com.codeflix.catalog.admin.infrastructure.api.controllers;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.codeflix.catalog.admin.application.category.create.CreateCategoryCommand;
import com.codeflix.catalog.admin.application.category.create.CreateCategoryOutput;
import com.codeflix.catalog.admin.application.category.create.CreateCategoryUseCase;
import com.codeflix.catalog.admin.application.category.retrieve.get.GetCategoryByIdUseCase;
import com.codeflix.catalog.admin.domain.pagination.Pagination;
import com.codeflix.catalog.admin.domain.validation.handler.Notification;
import com.codeflix.catalog.admin.infrastructure.api.CategoryAPI;
import com.codeflix.catalog.admin.infrastructure.category.models.CategoryApiOutput;
import com.codeflix.catalog.admin.infrastructure.category.models.CreateCategoryApiInput;
import com.codeflix.catalog.admin.infrastructure.category.presenters.CategoryApiPresenter;

@RestController
public class CategoryController implements CategoryAPI {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;

    public CategoryController(
            final CreateCategoryUseCase createCategoryUseCase,
            final GetCategoryByIdUseCase getCategoryByIdUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase);
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

    @Override
    public CategoryApiOutput getById(String id) {
        return CategoryApiPresenter.present(this.getCategoryByIdUseCase.execute(id));
    }

}
