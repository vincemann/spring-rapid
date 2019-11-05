package io.github.vincemann.generic.crud.lib.service.decorator.implementations;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * must be the outest Decorator, because overriding will kill transactional functionality
 * @param <E>
 * @param <Id>
 */
public class TransactionalCrudServiceDecorator<E extends IdentifiableEntity<Id>, Id extends Serializable> implements CrudService<E, Id> {
    private CrudService<E,Id> crudService;

    public TransactionalCrudServiceDecorator(CrudService<E,Id> crudService) {
        this.crudService = crudService;
    }

    @Transactional
    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        return crudService.findById(id);
    }

    @Transactional
    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        return crudService.update(entity);
    }

    @Transactional
    @Override
    public E save(E entity) throws BadEntityException {
        return crudService.save(entity);
    }

    @Transactional
    @Override
    public Set<E> findAll() {
        return crudService.findAll();
    }

    @Transactional
    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        crudService.delete(entity);
    }

    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        crudService.deleteById(id);
    }

    @Override
    public Class<E> getEntityClass() {
        return crudService.getEntityClass();
    }
}
