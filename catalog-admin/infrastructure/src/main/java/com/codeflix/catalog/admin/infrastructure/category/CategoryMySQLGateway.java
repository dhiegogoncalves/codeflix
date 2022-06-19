package com.codeflix.catalog.admin.infrastructure.category;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.codeflix.catalog.admin.domain.category.Category;
import com.codeflix.catalog.admin.domain.category.CategoryGateway;
import com.codeflix.catalog.admin.domain.category.CategoryID;
import com.codeflix.catalog.admin.domain.category.CategorySearchQuery;
import com.codeflix.catalog.admin.domain.pagination.Pagination;
import com.codeflix.catalog.admin.infrastructure.category.persistence.CategoryRepository;

@Service
public class CategoryMySQLGateway implements CategoryGateway {

    private final CategoryRepository categoryRepository;

    public CategoryMySQLGateway(final CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(Category aCategory) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteById(CategoryID anId) {
        // TODO Auto-generated method stub

    }

    @Override
    public Optional<Category> findById(CategoryID anId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Category update(Category aCategory) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Pagination<Category> findAll(CategorySearchQuery aQuery) {
        // TODO Auto-generated method stub
        return null;
    }
}
