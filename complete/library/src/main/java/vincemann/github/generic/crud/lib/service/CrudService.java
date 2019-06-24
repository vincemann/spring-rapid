package vincemann.github.generic.crud.lib.service;

import vincemann.github.generic.crud.lib.model.IdentifiableEntity;
import vincemann.github.generic.crud.lib.service.exception.BadEntityException;
import vincemann.github.generic.crud.lib.service.exception.EntityNotFoundException;
import vincemann.github.generic.crud.lib.service.exception.NoIdException;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for a Service offering Crud Operations
 * @param <E>       Type of managed Entity
 * @param <Id>      Id Type of managed Entity
 */
public interface CrudService<E extends IdentifiableEntity<Id>,Id extends Serializable> {

    Optional<E> findById(Id id) throws NoIdException;

    E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException;

    E save(E entity) throws  BadEntityException;

    Set<E> findAll();

    void delete(E entity) throws EntityNotFoundException, NoIdException;

    void deleteById(Id id) throws EntityNotFoundException, NoIdException;
}