package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Works like an abstract class, that only delegates to next element in chain, without the limitation of needing to be extended rather than implemented.
 * User together with {@link ServiceExtension}
 * @param <S>
 */
public interface CrudServiceExtension<S extends CrudService>
            extends CrudService, NextLinkAware<S>{


    public S getNext();

    @Override
    default Optional findById(Serializable id) throws BadEntityException {
        return getNext().findById(id);
    }

    @Override
    default IdentifiableEntity update(IdentifiableEntity entity, Boolean full) throws EntityNotFoundException, BadEntityException {
        return getNext().update(entity,full);
    }

    @Override
    default IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        return getNext().save(entity);
    }

    @Override
    default void deleteById(Serializable id) throws EntityNotFoundException, BadEntityException {
        getNext().deleteById(id);
    }

    @Override
    public default Set<IdentifiableEntity> findAll() {
        return getNext().findAll();
    }

    @Override
    public default Class<IdentifiableEntity> getEntityClass() {
        return getNext().getEntityClass();
    }

    @Override
    public default CrudRepository getRepository() {
        return getNext().getRepository();
    }

    @Override
    public default Class<?> getTargetClass() {
        return getNext().getTargetClass();
    }
}
