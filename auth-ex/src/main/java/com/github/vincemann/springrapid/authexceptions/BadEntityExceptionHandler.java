package com.github.vincemann.springrapid.authexceptions;

import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.exceptionsapi.AbstractBadRequestExceptionHandler;

public class BadEntityExceptionHandler extends AbstractBadRequestExceptionHandler<BadEntityException> {

    public BadEntityExceptionHandler() {
        super(BadEntityException.class);
    }
}
