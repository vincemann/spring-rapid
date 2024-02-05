package com.github.vincemann.springrapid.limitsaves;

import com.github.vincemann.springrapid.exceptionsapi.AbstractExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Order(Ordered.LOWEST_PRECEDENCE)
public class TooManyRequestsExceptionHandler extends AbstractExceptionHandler<TooManyRequestsException> {
    public TooManyRequestsExceptionHandler() {
        super(TooManyRequestsException.class);
    }

    @Override
    public HttpStatus getStatus(TooManyRequestsException ex) {
        return HttpStatus.TOO_MANY_REQUESTS;
    }
}
