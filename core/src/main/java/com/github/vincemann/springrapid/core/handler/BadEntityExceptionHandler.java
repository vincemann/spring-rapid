package com.github.vincemann.springrapid.core.handler;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.exceptionsapi.AbstractBadRequestExceptionHandler;

public class BadEntityExceptionHandler extends AbstractBadRequestExceptionHandler<BadEntityException> {

    public BadEntityExceptionHandler() {
        super(BadEntityException.class);
    }
}
