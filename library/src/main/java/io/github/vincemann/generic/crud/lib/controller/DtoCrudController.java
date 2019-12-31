package io.github.vincemann.generic.crud.lib.controller;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Collection;


/**
 * Defines Crud Operations a DtoCrudController must implement
 * @param <Dto>     Dto Entity Type of corresponding ServiceEntity, managed by this DtoCrudController
 * @param <Id>      Id Type of corresponding Service Entity of  {@link Dto}
 */
public interface DtoCrudController<Dto extends IdentifiableEntity<Id>,Id extends Serializable> {

    //todo impl methods that only return ids and not whole dtos

    ResponseEntity<Dto> create(Dto entity) throws BadEntityException, EntityMappingException;

    ResponseEntity<Dto> find(Id id) throws NoIdException, EntityNotFoundException, EntityMappingException;

    ResponseEntity<Dto> update(Dto entity) throws EntityMappingException, BadEntityException, NoIdException, EntityNotFoundException;

    ResponseEntity delete(Id id) throws NoIdException, EntityNotFoundException;

    ResponseEntity<Collection<Dto>> findAll() throws EntityMappingException;
}
