package com.codeflix.catalog.admin.domain.exceptions;

import java.util.Collections;
import java.util.List;

import com.codeflix.catalog.admin.domain.AggregateRoot;
import com.codeflix.catalog.admin.domain.Identifier;
import com.codeflix.catalog.admin.domain.validation.Error;

public class NotFoundException extends DomainException {

    protected NotFoundException(final String aMessage, final List<Error> anErrors) {
        super(aMessage, anErrors);
    }

    public static NotFoundException with(
            final Class<? extends AggregateRoot<?>> anAggregate, final Identifier id) {
        final var anError = "%s with ID %s was not found"
                .formatted(anAggregate.getSimpleName(), id.getValue());

        return new NotFoundException(anError, Collections.emptyList());
    }
}
