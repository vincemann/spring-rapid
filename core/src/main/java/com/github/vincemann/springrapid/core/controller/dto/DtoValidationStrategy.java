package com.github.vincemann.springrapid.core.controller.dto;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Set;


public interface DtoValidationStrategy extends AopLoggable {

    /**
     * checks whether the Dto entity, read from the {@link HttpServletRequest} is valid
     * @param dto           Dto Entity read from the {@link HttpServletRequest}
     * @throws ConstraintViolationException     is thrown, when Dto Entity {@param dto} is not valid
     */
    @LogInteraction(Severity.TRACE)
    void validate(Object dto) throws ConstraintViolationException;


    @LogInteraction(Severity.TRACE)
    void validatePartly(Object patchDto, Set<String> updatedFields);
}
