package io.github.vincemann.springrapid.core.controller;

import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMappingException;
import io.github.vincemann.springrapid.core.controller.rapid.DtoSerializingException;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.exception.NoIdException;

import java.io.Serializable;
import java.util.Collection;


/**
 * CrudController with variable Dtos.
 * @param <Id>      Id Type of the Entity which's crud operations are exposed with this Controller
 */
public interface DtoCrudController<Id extends Serializable> {

    IdentifiableEntity<Id> create(IdentifiableEntity<Id> entity) throws BadEntityException, DtoMappingException, DtoSerializingException;

    IdentifiableEntity<Id> find(Id id) throws NoIdException, EntityNotFoundException, DtoMappingException, DtoSerializingException;

    /**
     *
     * @param entity
     * @param full      indicates whether all set values should be recognized as new values or only non null values
     *                  If you want to delete values (aka setting them null) with the update, then set this flag to true.
     * @return
     * @throws DtoMappingException
     * @throws BadEntityException
     * @throws NoIdException
     * @throws EntityNotFoundException
     */
    IdentifiableEntity<Id> update(IdentifiableEntity<Id> entity, boolean full) throws DtoMappingException, BadEntityException, NoIdException, EntityNotFoundException, DtoSerializingException;

    void delete(Id id) throws NoIdException, EntityNotFoundException;

    Collection<IdentifiableEntity<Id>> findAll() throws DtoMappingException, DtoSerializingException;
}
