package io.github.vincemann.generic.crud.lib.controller;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;


/**
 * Defines Crud Operations a DTOCrudController must implement
 * @param <DTO>     DTO Entity Type of corresponding ServiceEntity, managed by this DTOCrudController
 * @param <Id>      Id Type of corresponding Service Entity of  {@link DTO}
 */
public interface DTOCrudController<DTO extends IdentifiableEntity<Id>,Id extends Serializable & Comparable> {

    //todo impl methods that only return ids and not whole dtos

    ResponseEntity<DTO> create(DTO entity) throws BadEntityException, EntityMappingException;

    ResponseEntity<DTO> find(Id id) throws NoIdException, EntityNotFoundException;

    ResponseEntity<DTO> update(DTO entity) throws EntityMappingException, BadEntityException, NoIdException, EntityNotFoundException;

    ResponseEntity delete(Id id) throws NoIdException, EntityNotFoundException;
}
