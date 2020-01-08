package io.github.vincemann.generic.crud.lib.controller;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.util.Collection;


/**
 * Defines Crud Operations a DtoCrudController must implement
 * @param <Id>      Id Type of corresponding Service Entity
 */
public interface DtoCrudController<Id extends Serializable> {

    ResponseEntity<? extends IdentifiableEntity<Id>> create(IdentifiableEntity<Id> entity) throws BadEntityException, EntityMappingException;

    ResponseEntity<? extends IdentifiableEntity<Id>> find(Id id) throws NoIdException, EntityNotFoundException, EntityMappingException;

    ResponseEntity<? extends IdentifiableEntity<Id>> update(IdentifiableEntity<Id> entity) throws EntityMappingException, BadEntityException, NoIdException, EntityNotFoundException;

    ResponseEntity<?> delete(Id id) throws NoIdException, EntityNotFoundException;

    ResponseEntity<Collection<IdentifiableEntity<Id>>> findAll() throws EntityMappingException;
}
