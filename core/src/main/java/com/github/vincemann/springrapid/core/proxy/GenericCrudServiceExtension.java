package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.aoplog.api.LogConfig;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

//override log config from CrudService -> explicitly enable Logging for methods that actually matter in subclasses
@LogInteraction(disabled = true)
//@LogConfig
public interface GenericCrudServiceExtension<S extends CrudService<E,Id>,E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends CrudService<E,Id>, NextLinkAware<S>{

    @Override
    default Optional<E> findById(Id id) throws BadEntityException {
        return getNext().findById(id);
    }

    @Override
    default E softUpdate(E entity) throws EntityNotFoundException, BadEntityException {
        return getNext().softUpdate(entity);
    }

    @Override
    default E partialUpdate(E entity, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        return getNext().partialUpdate(entity,fieldsToRemove);
    }

    @Override
    default E update(E entity) throws BadEntityException, EntityNotFoundException {
        return getNext().update(entity);
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

}
