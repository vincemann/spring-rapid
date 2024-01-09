package com.github.vincemann.springrapid.auth.handler;

import com.github.vincemann.springrapid.auth.ErrorCodes;
import com.github.vincemann.springrapid.exceptionsapi.AbstractExceptionHandler;
import com.github.vincemann.springrapid.auth.service.AlreadyRegisteredException;
import org.springframework.http.HttpStatus;

public class AlreadyRegisteredExceptionHandler extends AbstractExceptionHandler<AlreadyRegisteredException> {

    public AlreadyRegisteredExceptionHandler() {
        super(AlreadyRegisteredException.class);
    }

    @Override
    public HttpStatus getStatus(AlreadyRegisteredException ex) {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getExceptionId(AlreadyRegisteredException ex) {
        return String.valueOf(ErrorCodes.ALREADY_REGISTERED);
    }
}