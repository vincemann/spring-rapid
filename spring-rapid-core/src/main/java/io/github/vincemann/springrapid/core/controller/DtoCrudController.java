package io.github.vincemann.springrapid.core.controller;

import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.rapid.DtoSerializingException;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.Collection;


/**
 * CrudController with variable Dtos.
 * @param <Id>      Id Type of the Entity which's crud operations are exposed with this Controller
 */
public interface DtoCrudController<Id extends Serializable> {

    Object create(Object dto) throws BadEntityException, DtoMappingException, DtoSerializingException;

    Object find(Id id) throws BadEntityException, EntityNotFoundException, DtoMappingException, DtoSerializingException;

    /**
     *
     * @param dto
     * @param full      indicates whether all set values should be recognized as new values or only non null values
     *                  If you want to delete values (aka setting them null) with the update, then set this flag to true.
     * @return
     * @throws DtoMappingException
     * @throws BadEntityException
     * @throws BadEntityException
     * @throws EntityNotFoundException
     */
    Object update(Object dto, boolean full) throws DtoMappingException, BadEntityException, BadEntityException, EntityNotFoundException, DtoSerializingException;

    void delete(Id id) throws BadEntityException, EntityNotFoundException;

    Collection<Object> findAll() throws DtoMappingException, DtoSerializingException;
}
