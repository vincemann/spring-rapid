package io.github.vincemann.springrapid.core.bootstrap;

public class DatabaseInitException extends Exception {
    public DatabaseInitException() {
    }

    public DatabaseInitException(String message) {
        super(message);
    }

    public DatabaseInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseInitException(Throwable cause) {
        super(cause);
    }

    public DatabaseInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
