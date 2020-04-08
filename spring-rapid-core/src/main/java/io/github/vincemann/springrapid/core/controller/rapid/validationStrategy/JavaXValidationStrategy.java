package io.github.vincemann.springrapid.core.controller.rapid.validationStrategy;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;

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
    public void validateDto(IdentifiableEntity<Id> dto) throws ConstraintViolationException {
        Set<ConstraintViolation<IdentifiableEntity<Id>>> constraintViolations = validator.validate(dto);
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
