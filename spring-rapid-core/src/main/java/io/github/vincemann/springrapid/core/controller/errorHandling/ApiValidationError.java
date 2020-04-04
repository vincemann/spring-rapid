package io.github.vincemann.springrapid.core.controller.errorHandling;

import lombok.Data;


@Data
class ApiValidationError extends ApiSubError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    public ApiValidationError(String object, String field, Object rejectedValue, String message) {
        this.object = object;
        this.field = field;
        this.rejectedValue = rejectedValue;
        this.message = message;
    }

    ApiValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }
}