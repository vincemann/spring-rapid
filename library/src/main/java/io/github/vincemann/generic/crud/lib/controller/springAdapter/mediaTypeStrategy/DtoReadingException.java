package io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy;

public class DtoReadingException extends Exception {
    public DtoReadingException() {
    }

    public DtoReadingException(String message) {
        super(message);
    }

    public DtoReadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DtoReadingException(Throwable cause) {
        super(cause);
    }

    public DtoReadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
