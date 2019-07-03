package io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 *
 * @param <DTO>     Type of DTO Entity
 * @param <Id>      Id Type of id that is send to Server by Client
 */
public interface ValidationStrategy<DTO,Id> {

    /**
     * checks whether the DTO entity, read from the {@link HttpServletRequest} is valid
     * @param dto           DTO Entity read from the {@link HttpServletRequest}
     * @param httpServletRequest    HttpRequest from client
     * @throws ConstraintViolationException     is thrown, when DTO Entity {@param dto} is not valid
     */
    public void validateDTO(DTO dto, HttpServletRequest httpServletRequest) throws ConstraintViolationException;

    /**
     * checks whether the Id, read from the {@link HttpServletRequest} is valid
     * @param id    Id, read from the {@link HttpServletRequest}
     * @param httpServletRequest    HttpRequest from client
     * @throws ConstraintViolationException     is thrown, when DTO Entity {@param id} is not valid
     */
    public void validateId(Id id,HttpServletRequest httpServletRequest) throws ConstraintViolationException;
}
