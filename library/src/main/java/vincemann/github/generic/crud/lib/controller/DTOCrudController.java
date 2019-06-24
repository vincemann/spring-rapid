package vincemann.github.generic.crud.lib.controller;

import vincemann.github.generic.crud.lib.controller.exception.EntityMappingException;
import vincemann.github.generic.crud.lib.model.IdentifiableEntity;
import vincemann.github.generic.crud.lib.service.exception.BadEntityException;
import vincemann.github.generic.crud.lib.service.exception.EntityNotFoundException;
import vincemann.github.generic.crud.lib.service.exception.NoIdException;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

public interface DTOCrudController<DTO extends IdentifiableEntity<Id>,Id extends Serializable> {

    //todo methoden einbauen die einfach nur die id returnen


    ResponseEntity<DTO> create(DTO entity) throws EntityMappingException, BadEntityException;

    ResponseEntity<DTO> find(Id id) throws EntityMappingException, NoIdException, EntityNotFoundException;

    ResponseEntity<DTO> update(DTO entity) throws EntityMappingException, NoIdException, EntityNotFoundException, BadEntityException;

    ResponseEntity delete(Id id) throws NoIdException, EntityNotFoundException;
}
