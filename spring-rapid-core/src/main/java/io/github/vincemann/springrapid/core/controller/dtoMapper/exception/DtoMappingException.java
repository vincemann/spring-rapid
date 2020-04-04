package io.github.vincemann.springrapid.core.controller.dtoMapper.exception;

public class DtoMappingException extends Exception{

    public DtoMappingException() {
    }

    public DtoMappingException(String message) {
        super(message);
    }

    public DtoMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public DtoMappingException(Throwable cause) {
        super(cause);
    }

    public DtoMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
