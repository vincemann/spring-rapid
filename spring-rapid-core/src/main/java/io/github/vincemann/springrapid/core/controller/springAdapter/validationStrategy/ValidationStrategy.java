package io.github.vincemann.springrapid.core.controller.springAdapter.validationStrategy;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;

/**
 *
 * @param <Id>      Id Type of id that is send to Server by Client
 */
public interface ValidationStrategy<Id extends Serializable> {

    /**
     * checks whether the Dto entity, read from the {@link HttpServletRequest} is valid
     * @param dto           Dto Entity read from the {@link HttpServletRequest}
     * @param httpServletRequest    HttpRequest from client
     * @throws ConstraintViolationException     is thrown, when Dto Entity {@param dto} is not valid
     */
    public abstract void validateDto(IdentifiableEntity<Id> dto, HttpServletRequest httpServletRequest) throws ConstraintViolationException;

    /**
     * checks whether the Id, read from the {@link HttpServletRequest} is valid
     * @param id    Id, read from the {@link HttpServletRequest}
     * @param httpServletRequest    HttpRequest from client
     * @throws ConstraintViolationException     is thrown, when Dto Entity {@param id} is not valid
     */
    public abstract void validateId(Id id,HttpServletRequest httpServletRequest) throws ConstraintViolationException;

}
