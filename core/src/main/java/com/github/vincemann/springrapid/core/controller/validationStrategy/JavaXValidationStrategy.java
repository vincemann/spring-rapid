package com.github.vincemann.springrapid.core.controller.validationStrategy;

import javax.validation.*;
import java.io.Serializable;
import java.util.Set;

/**
 * BaseImpl of {@link ValidationStrategy}, that utilizes the javax validation API.
 * See: {@link Validator}
 * @param <Id>
 */
public class JavaXValidationStrategy<Id extends Serializable> implements ValidationStrategy<Id>{
    private final Validator validator;

    public JavaXValidationStrategy(Validator validator) {
//        ValidatorFactory factory=Validation.buildDefaultValidatorFactory();
//        this.validator=factory.getValidator();
        this.validator = validator;
    }

    @Override
    public void validateDto(Object dto) throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(dto);
        if(!constraintViolations.isEmpty())
            throw new ConstraintViolationException(constraintViolations);
    }

    @Override
    public void validateId(Id id) throws ConstraintViolationException {
        Set<ConstraintViolation<Id>> constraintViolations = validator.validate(id);
        if(!constraintViolations.isEmpty())
            throw new ConstraintViolationException(constraintViolations);
    }

}
