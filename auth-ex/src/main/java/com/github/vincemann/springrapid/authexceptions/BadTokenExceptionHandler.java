package com.github.vincemann.springrapid.authexceptions;

import com.github.vincemann.springrapid.auth.jwt.BadTokenException;
import com.github.vincemann.springrapid.lemon.exceptions.AbstractExceptionHandler;
import org.springframework.http.HttpStatus;

public class BadTokenExceptionHandler extends AbstractExceptionHandler<BadTokenException> {

    public BadTokenExceptionHandler() {
        super(BadTokenException.class);
    }

    @Override
    public HttpStatus getStatus(BadTokenException ex) {
        return HttpStatus.UNAUTHORIZED;
    }
}
