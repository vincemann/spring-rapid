package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//override log config from CrudService -> explicitly enable Logging for methods that actually matter in subclasses
@LogInteraction(disabled = true)
//@LogConfig
public interface GenericCrudServiceExtension
        <S extends CrudService<E,Id>,E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends CrudService<E,Id>, NextLinkAware<S>{

    @Override
    default Optional<E> findById(Id id) {
        return getNext().findById(id);
    }

    @Override
    default E softUpdate(E entity) throws EntityNotFoundException, BadEntityException {
        return getNext().softUpdate(entity);
    }

    @Override
    default E partialUpdate(E entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException {
        return getNext().partialUpdate(entity, fieldsToUpdate);
    }

    @Override
    default Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> entityFilters, List<EntitySortingStrategy<? super E>> sortingStrategies){
        return getNext().findAll(jpqlFilters,entityFilters,sortingStrategies);
    }


    @Override
    default Set<E> findSome(Set<Id> ids) {
        return getNext().findSome(ids);
    }

    @Override
    default E fullUpdate(E entity) throws BadEntityException, EntityNotFoundException {
        return getNext().fullUpdate(entity);
    }

    @Override
    default E save(E entity) throws BadEntityException {
        return getNext().save(entity);
    }

    @Override
    default void deleteById(Id id) throws EntityNotFoundException {
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
