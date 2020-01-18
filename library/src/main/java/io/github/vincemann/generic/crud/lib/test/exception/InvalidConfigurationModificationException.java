package io.github.vincemann.generic.crud.lib.test.exception;

public class InvalidConfigurationModificationException extends RuntimeException {
    public InvalidConfigurationModificationException() {
    }

    public InvalidConfigurationModificationException(String message) {
        super(message);
    }

    public InvalidConfigurationModificationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigurationModificationException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigurationModificationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
