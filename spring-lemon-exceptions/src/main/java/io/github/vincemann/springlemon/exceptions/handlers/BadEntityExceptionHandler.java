package io.github.vincemann.springlemon.exceptions.handlers;

import io.github.vincemann.springrapid.core.service.exception.BadEntityException;

public class BadEntityExceptionHandler extends AbstractBadRequestExceptionHandler<BadEntityException> {

    public BadEntityExceptionHandler() {
        super(BadEntityException.class);
    }
}
