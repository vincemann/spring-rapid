package io.github.vincemann.springrapid.core.handler;

import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lemon.exceptions.handlers.AbstractBadRequestExceptionHandler;
import org.springframework.stereotype.Component;

@Component
public class BadEntityExceptionHandler extends AbstractBadRequestExceptionHandler<BadEntityException> {

    public BadEntityExceptionHandler() {
        super(BadEntityException.class);
    }
}
