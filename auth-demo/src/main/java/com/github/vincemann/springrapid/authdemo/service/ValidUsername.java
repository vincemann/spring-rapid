package com.github.vincemann.springrapid.authdemo.service;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// does not check for uniqueness!
@NotBlank(message = "{blank.name}")
@Size(min = ValidUsername.MIN_SIZE, max = ValidUsername.MAX_SIZE, message = "invalid name size ( 2 - 20 )")
@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "The name must be alphanumeric")
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidUsername {

    int MAX_SIZE = 20;
    int MIN_SIZE = 2;

    String message() default "invalid username";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
