package com.github.vincemann.springlemon.auth.handler;

import com.github.vincemann.springlemon.auth.service.token.BadTokenException;
import com.github.vincemann.springlemon.exceptions.handlers.AbstractExceptionHandler;
import org.springframework.http.HttpStatus;

public class BadTokenExceptionHandler extends AbstractExceptionHandler<BadTokenException> {

    public BadTokenExceptionHandler() {
        super(BadTokenException.class);
    }

    @Override
    protected HttpStatus getStatus(BadTokenException ex) {
        return HttpStatus.UNAUTHORIZED;
    }
}
