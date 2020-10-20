package com.github.vincemann.springlemon.exceptions.util;

import com.github.vincemann.springlemon.exceptions.ExceptionIdMaker;
import com.github.vincemann.springlemon.exceptions.MultiErrorException;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolationException;

import static com.github.vincemann.springlemon.exceptions.util.LemonExceptionUtils.getExceptionId;

public class Validate {



    /**
     * Creates a MultiErrorException out of the given parameters
     */
    public static MultiErrorException condition(
            boolean valid, String messageKey, Object... args) {

        return field(null, valid, messageKey, args);
    }

    /**
     * Creates a MultiErrorException out of the given parameters
     */
    public static MultiErrorException field(
            String fieldName, boolean valid, String messageKey, Object... args) {

        return new MultiErrorException().validateField(fieldName, valid, messageKey, args);
    }


    /**
     * Creates a MultiErrorException out of the constraint violations in the given bean
     */
    public static <T> MultiErrorException bean(String beanName, T bean, Class<?>... validationGroups) {

        return new MultiErrorException()
                .exceptionId(getExceptionId(new ConstraintViolationException(null)))
                .validationGroups(validationGroups)
                .validateBean(beanName, bean);
    }
}
