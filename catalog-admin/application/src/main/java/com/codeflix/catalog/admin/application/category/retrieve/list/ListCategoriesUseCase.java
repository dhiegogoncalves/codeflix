package com.codeflix.catalog.admin.application.category.retrieve.list;

import com.codeflix.catalog.admin.application.UserCase;
import com.codeflix.catalog.admin.domain.category.CategorySearchQuery;
import com.codeflix.catalog.admin.domain.pagination.Pagination;

public abstract class ListCategoriesUseCase extends UserCase<CategorySearchQuery, Pagination<CategoryListOutput>> {

}
