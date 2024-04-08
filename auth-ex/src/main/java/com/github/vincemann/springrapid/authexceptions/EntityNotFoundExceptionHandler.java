package com.github.vincemann.springrapid.authexceptions;

import com.github.vincemann.springrapid.auth.EntityNotFoundException;
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
