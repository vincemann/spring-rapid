package io.github.vincemann.generic.crud.lib.controller;

import io.github.vincemann.generic.crud.lib.controller.exception.EntityMappingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

public interface DTOCrudController<DTO extends IdentifiableEntity<Id>,Id extends Serializable> {

    //todo methoden einbauen die einfach nur die id returnen


    ResponseEntity<DTO> create(DTO entity) throws EntityMappingException, BadEntityException;

    ResponseEntity<DTO> find(Id id) throws EntityMappingException, NoIdException, EntityNotFoundException;

    ResponseEntity<DTO> update(DTO entity) throws EntityMappingException, NoIdException, EntityNotFoundException, BadEntityException;

    ResponseEntity delete(Id id) throws NoIdException, EntityNotFoundException;
}
