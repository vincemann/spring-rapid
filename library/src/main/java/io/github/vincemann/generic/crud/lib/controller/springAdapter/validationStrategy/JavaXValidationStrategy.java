package io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.validation.*;
import java.util.Set;

/**
 * BaseImpl of {@link ValidationStrategy}, that utilizes the javax validation API.
 * See: {@link Validator}
 * @param <Dto>
 * @param <Id>
 */
public class JavaXValidationStrategy<Dto,Id> implements ValidationStrategy<Dto,Id>{
    private final Validator validator;

    public JavaXValidationStrategy() {
        ValidatorFactory factory=Validation.buildDefaultValidatorFactory();
        this.validator=factory.getValidator();
    }

    @Override
    public void validateDto(Dto dto, HttpServletRequest httpServletRequest) throws ConstraintViolationException {
        Set<ConstraintViolation<Dto>> constraintViolations = validator.validate(dto);
        if(!constraintViolations.isEmpty())
            throw new ConstraintViolationException(constraintViolations);
    }

    @Override
    public void validateId(Id id, HttpServletRequest httpServletRequest) throws ConstraintViolationException {
        Set<ConstraintViolation<Id>> constraintViolations = validator.validate(id);
        if(!constraintViolations.isEmpty())
            throw new ConstraintViolationException(constraintViolations);
    }
}
