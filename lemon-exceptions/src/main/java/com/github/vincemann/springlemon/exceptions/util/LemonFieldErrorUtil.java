package com.github.vincemann.springlemon.exceptions.util;

import com.github.vincemann.springlemon.exceptions.FieldError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;

public class LemonFieldErrorUtil {

    // helper methods for method above
    public static FieldError of(org.springframework.validation.FieldError fieldError) {
        return new FieldError(fieldError.getObjectName() + "." + fieldError.getField(),
                fieldError.getCode(), fieldError.getDefaultMessage());
    }

    public static FieldError of(ObjectError error) {
        return new FieldError(error.getObjectName(),
                error.getCode(), error.getDefaultMessage());
    }

    /**
     * Converts a ConstraintViolation
     * to a FieldError
     */
    // helper method for method above
    public static FieldError of(ConstraintViolation<?> constraintViolation) {
        // Get the field name by removing the first part of the propertyPath.
        // (The first part would be the service method name)
        String field = StringUtils.substringAfter(
                constraintViolation.getPropertyPath().toString(), ".");

        return new FieldError(field,
                constraintViolation.getMessageTemplate(),
                constraintViolation.getMessage());
    }
}
