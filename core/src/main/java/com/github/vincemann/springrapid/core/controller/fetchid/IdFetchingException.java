package com.github.vincemann.springrapid.core.controller.fetchid;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

/**
 * Indicates that the id could not be Fetched from the {@link javax.servlet.http.HttpServletRequest}.
 * See : {@link IdFetchingStrategy}
 */
public class IdFetchingException extends BadEntityException {
    public IdFetchingException() {
    }

    public IdFetchingException(String message) {
        super(message);
    }

    public IdFetchingException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdFetchingException(Throwable cause) {
        super(cause);
    }

    public IdFetchingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
