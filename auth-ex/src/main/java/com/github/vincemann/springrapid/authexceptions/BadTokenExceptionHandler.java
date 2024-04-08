package com.github.vincemann.springrapid.authexceptions;

import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.exceptionsapi.AbstractExceptionHandler;
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
