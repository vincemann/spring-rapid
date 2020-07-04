package com.github.vincemann.springrapid.core.controller.validationStrategy;

import com.github.vincemann.aoplog.Severity;
import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;

/**
 *
 * @param <Id>      Id Type of id that is send to Server by Client
 */
public interface ValidationStrategy<Id extends Serializable> extends AopLoggable {

    /**
     * checks whether the Dto entity, read from the {@link HttpServletRequest} is valid
     * @param dto           Dto Entity read from the {@link HttpServletRequest}
     * @throws ConstraintViolationException     is thrown, when Dto Entity {@param dto} is not valid
     */
    @LogInteraction
    public abstract void validateDto(Object dto) throws ConstraintViolationException;

    /**
     * checks whether the Id, read from the {@link HttpServletRequest} is valid
     * @param id    Id, read from the {@link HttpServletRequest}
     * @throws ConstraintViolationException     is thrown, when Dto Entity {@param id} is not valid
     */
    @LogInteraction(Severity.TRACE)
    public abstract void validateId(Id id) throws ConstraintViolationException;

}
