package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

public interface GenericSimpleCrudServiceExtension<S extends SimpleCrudService<E,Id>,E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends SimpleCrudService<E,Id>, NextLinkAware<S>{

    @Override
    default Optional<E> findById(Id id) throws BadEntityException {
        return getNext().findById(id);
    }

    @Override
    default E update(E entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }

    @Override
    default E save(E entity) throws BadEntityException {
        return getNext().save(entity);
    }

    @Override
    default void deleteById(Id id) throws EntityNotFoundException, BadEntityException {
        getNext().deleteById(id);
    }

    @Override
    public default Set<E> findAll() {
        return getNext().findAll();
    }

    @Override
    default Class<E> getEntityClass() {
        return getNext().getEntityClass();
    }

    @Override
    default Class<?> getTargetClass() {
        return getNext().getTargetClass();
    }
}
