package com.codeflix.catalog.admin.application;

import com.codeflix.catalog.admin.domain.category.Category;

public abstract class UserCase<IN, OUT> {

    public abstract OUT execute(IN anIn);
}