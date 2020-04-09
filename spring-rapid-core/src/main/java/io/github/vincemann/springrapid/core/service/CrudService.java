package io.github.vincemann.springrapid.core.service;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.exception.NoIdException;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for a Service offering Crud Operations
 * @param <E>       Type of managed Entity
 * @param <Id>      Id Type of managed Entity
 */
@ServiceComponent
public interface CrudService
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E,Id>
        >
{

    Optional<E> findById(Id id) throws NoIdException;

    /**
     * If full is false, then
     * this function only applies changes of non null fields.
     * Therefore it is not capable of setting a field in the entityToUpdate null.
     * If full is true, then
     * This function applies all changes, therefore it is capable of setting values to null aka removing them.
     * @param entity
     * @return
     * @throws EntityNotFoundException
     * @throws NoIdException
     * @throws BadEntityException
     */
    E update(E entity, Boolean full) throws EntityNotFoundException, NoIdException, BadEntityException;

    E save(E entity) throws  BadEntityException;

    Set<E> findAll();

    void deleteById(Id id) throws EntityNotFoundException, NoIdException;

    Class<E> getEntityClass();

    R getRepository();
}