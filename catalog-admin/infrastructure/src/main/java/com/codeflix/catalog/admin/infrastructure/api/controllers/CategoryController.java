package com.codeflix.catalog.admin.infrastructure.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.codeflix.catalog.admin.domain.pagination.Pagination;
import com.codeflix.catalog.admin.infrastructure.api.CategoryAPI;

@RestController
public class CategoryController implements CategoryAPI {

    @Override
    public ResponseEntity<?> createCategory() {
        return null;
    }

    @Override
    public Pagination<?> listCategories(String search, int page, int perPage, String sort, String direction) {
        return null;
    }

}
