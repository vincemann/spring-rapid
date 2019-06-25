package io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

public interface ValidationStrategy<DTO,Id> {

    public void validateDTO(DTO dto, HttpServletRequest httpServletRequest) throws ConstraintViolationException;
    public void validateId(Id id,HttpServletRequest httpServletRequest) throws ConstraintViolationException;
}
