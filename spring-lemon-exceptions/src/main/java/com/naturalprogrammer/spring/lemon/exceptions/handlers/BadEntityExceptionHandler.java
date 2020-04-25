package com.naturalprogrammer.spring.lemon.exceptions.handlers;

import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;

public class BadEntityExceptionHandler extends AbstractBadRequestExceptionHandler<BadEntityException> {

    public BadEntityExceptionHandler() {
        super(BadEntityException.class);
    }
}
