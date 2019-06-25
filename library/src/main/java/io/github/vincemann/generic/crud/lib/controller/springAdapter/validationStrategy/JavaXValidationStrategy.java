package io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.validation.*;
import java.util.Set;

public class JavaXValidationStrategy<DTO,Id> implements ValidationStrategy<DTO,Id>{
    private final Validator validator;

    public JavaXValidationStrategy() {
        ValidatorFactory factory=Validation.buildDefaultValidatorFactory();
        this.validator=factory.getValidator();
    }

    @Override
    public void validateDTO(DTO dto, HttpServletRequest httpServletRequest) throws ConstraintViolationException {
        Set<ConstraintViolation<DTO>> constraintViolations = validator.validate(dto);
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
