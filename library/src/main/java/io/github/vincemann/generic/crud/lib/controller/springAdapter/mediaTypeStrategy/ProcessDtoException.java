package io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy;

public class ProcessDtoException extends Exception {
    public ProcessDtoException() {
    }

    public ProcessDtoException(String message) {
        super(message);
    }

    public ProcessDtoException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProcessDtoException(Throwable cause) {
        super(cause);
    }

    public ProcessDtoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
