package com.github.vincemann.springrapid.core.handler;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.exceptionsapi.AbstractExceptionHandler;
import org.springframework.http.HttpStatus;

public class EntityNotFoundExceptionHandler extends AbstractExceptionHandler<EntityNotFoundException> {
    public EntityNotFoundExceptionHandler() {
        super(EntityNotFoundException.class);
    }

    @Override
    public HttpStatus getStatus(EntityNotFoundException ex) {
        return HttpStatus.NOT_FOUND;
    }
}
