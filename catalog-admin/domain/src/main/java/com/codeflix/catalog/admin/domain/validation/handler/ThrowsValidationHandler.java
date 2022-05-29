package com.codeflix.catalog.admin.domain.validation.handler;

import java.util.List;

import com.codeflix.catalog.admin.domain.exceptions.DomainException;
import com.codeflix.catalog.admin.domain.validation.Error;
import com.codeflix.catalog.admin.domain.validation.ValidationHandler;

public class ThrowsValidationHandler implements ValidationHandler {

    @Override
    public ValidationHandler append(Error anError) {
        throw DomainException.with(anError);
    }

    @Override
    public ValidationHandler append(ValidationHandler anHandler) {
        throw DomainException.with(anHandler.getErrors());
    }

    @Override
    public ValidationHandler validate(final Validation aValidation) {
        try {
            aValidation.validate();
        } catch (final Exception ex) {
            throw DomainException.with(new Error(ex.getMessage()));
        }

        return this;
    }

    @Override
    public List<Error> getErrors() {
        return List.of();
    }

}
