package com.github.vincemann.springrapid.auth.handler;

import com.github.vincemann.springlemon.exceptions.handlers.AbstractExceptionHandler;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AlreadyRegisteredExceptionHandler extends AbstractExceptionHandler<AlreadyRegisteredException> {

    public AlreadyRegisteredExceptionHandler() {
        super(AlreadyRegisteredException.class);
    }

    @Override
    protected HttpStatus getStatus(AlreadyRegisteredException ex) {
        return HttpStatus.BAD_REQUEST;
    }
}