package com.github.vincemann.springrapid.core.controller.dto;



import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Set;


public interface DtoValidationStrategy {

    /**
     * checks whether the Dto entity read from the {@link HttpServletRequest} is valid
     * @param dto           Dto Entity read from the {@link HttpServletRequest}
     * @throws ConstraintViolationException     is thrown, when Dto Entity {@param dto} is not valid
     */
    void validate(Object dto) throws ConstraintViolationException;
    void validatePartly(Object patchDto, Set<String> updatedFields);
}
