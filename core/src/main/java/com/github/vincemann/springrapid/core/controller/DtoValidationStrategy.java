package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;


public interface DtoValidationStrategy extends AopLoggable {

    /**
     * checks whether the Dto entity, read from the {@link HttpServletRequest} is valid
     * @param dto           Dto Entity read from the {@link HttpServletRequest}
     * @throws ConstraintViolationException     is thrown, when Dto Entity {@param dto} is not valid
     */
    @LogInteraction(Severity.TRACE)
    public abstract void validate(Object dto) throws ConstraintViolationException;


}
