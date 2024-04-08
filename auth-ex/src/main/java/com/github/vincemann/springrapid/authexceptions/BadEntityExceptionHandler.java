package com.github.vincemann.springrapid.authexceptions;

import com.github.vincemann.springrapid.lemon.exceptions.AbstractBadRequestExceptionHandler;
import com.github.vincemann.springrapid.auth.BadEntityException;


public class BadEntityExceptionHandler extends AbstractBadRequestExceptionHandler<BadEntityException> {

    public BadEntityExceptionHandler() {
        super(BadEntityException.class);
    }
}
