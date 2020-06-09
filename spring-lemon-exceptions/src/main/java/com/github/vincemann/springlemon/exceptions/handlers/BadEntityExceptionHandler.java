package com.github.vincemann.springlemon.exceptions.handlers;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public class BadEntityExceptionHandler extends AbstractBadRequestExceptionHandler<BadEntityException> {

    public BadEntityExceptionHandler() {
        super(BadEntityException.class);
    }
}
