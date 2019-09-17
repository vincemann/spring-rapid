package io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 *
 * @param <Dto>     Type of Dto Entity
 * @param <Id>      Id Type of id that is send to Server by Client
 */
public interface ValidationStrategy<Dto,Id> {

    /**
     * checks whether the Dto entity, read from the {@link HttpServletRequest} is valid
     * @param dto           Dto Entity read from the {@link HttpServletRequest}
     * @param httpServletRequest    HttpRequest from client
     * @throws ConstraintViolationException     is thrown, when Dto Entity {@param dto} is not valid
     */
    public void validateDto(Dto dto, HttpServletRequest httpServletRequest) throws ConstraintViolationException;

    /**
     * checks whether the Id, read from the {@link HttpServletRequest} is valid
     * @param id    Id, read from the {@link HttpServletRequest}
     * @param httpServletRequest    HttpRequest from client
     * @throws ConstraintViolationException     is thrown, when Dto Entity {@param id} is not valid
     */
    public void validateId(Id id,HttpServletRequest httpServletRequest) throws ConstraintViolationException;
}
