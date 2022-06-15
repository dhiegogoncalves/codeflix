package com.codeflix.catalog.admin.application;

public abstract class UserCase<IN, OUT> {

    public abstract OUT execute(IN anIn);
}