package io.github.vincemann.springrapid.core.service.exception;

public class NoIdException extends Exception{

    public NoIdException() {
    }

    public NoIdException(String message) {
        super(message);
    }

    public NoIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoIdException(Throwable cause) {
        super(cause);
    }

    public NoIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
