package io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import javax.servlet.http.HttpServletRequest;
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
    public void validateDto(IdentifiableEntity<Id> dto, HttpServletRequest httpServletRequest) throws ConstraintViolationException {
        Set<ConstraintViolation<IdentifiableEntity<Id>>> constraintViolations = validator.validate(dto);
        if(!constraintViolations.isEmpty())
            throw new ConstraintViolationException(constraintViolations);
    }

    @Override
    public void validateId(Id id, HttpServletRequest httpServletRequest) throws ConstraintViolationException {
        Set<ConstraintViolation<Id>> constraintViolations = validator.validate(id);
        if(!constraintViolations.isEmpty())
            throw new ConstraintViolationException(constraintViolations);
    }

    @Override
    public void beforeUpdateValidate(IdentifiableEntity<Id> dto) {
        if(dto.getId()==null){
            throw new ConstraintViolationException("Id must not be null for update entity request",null);
        }
    }

    @Override
    public void beforeCreateValidate(IdentifiableEntity<Id> dto) {
        if(dto.getId()!=null){
            throw new ConstraintViolationException("Id must be null for create entity request, the backend sets the id",null);
        }
    }
}
