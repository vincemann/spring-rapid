package com.github.vincemann.springrapid.auth.handler;

import com.github.vincemann.springrapid.auth.service.token.BadTokenException;
import com.github.vincemann.springrapid.exceptionsapi.AbstractExceptionHandler;
import org.springframework.http.HttpStatus;

// authorization token service throws bad token , other badTokens are catched and transformed to badEntity -> 400 code
public class BadTokenExceptionHandler extends AbstractExceptionHandler<BadTokenException> {

    public BadTokenExceptionHandler() {
        super(BadTokenException.class);
    }

    @Override
    public HttpStatus getStatus(BadTokenException ex) {
        return HttpStatus.UNAUTHORIZED;
    }
}
