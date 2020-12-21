package com.github.vincemann.springrapid.limitsaves;

import com.github.vincemann.springrapid.exceptionsapi.AbstractExceptionHandler;
import com.github.vincemann.springrapid.core.slicing.WebComponent;
import io.gitlab.vinceconrad.votesnackbackend.rapid.service.TooManyRequestsException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;

@WebComponent
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
