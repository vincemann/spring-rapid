package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Base class of decorators of crud services.
 * Provides delegating implementations of all methods of {@link CrudService}, so implementors can
 * overwrite only the methods they want to decorate.
 *
 * @param <S> decorated crud service
 * @param <E> entity type of crud service
 * @param <Id> id type of entity
 */
public abstract class CrudServiceDecorator<S extends CrudService<E,Id>,E extends IdentifiableEntity<Id>,Id extends Serializable>
    implements CrudService<E,Id>
{

    private S decorated;

    public CrudServiceDecorator(S decorated) {
        this.decorated = decorated;
    }

    @Override
    public String getBeanName() {
        return decorated.getBeanName();
    }

    @Override
    public Class<E> getEntityClass() {
        return decorated.getEntityClass();
    }

    @Override
    public Optional<E> findById(Id id) {
        return decorated.findById(id);
    }

    @Override
    public E findPresentById(Id id) throws EntityNotFoundException {
        return decorated.findPresentById(id);
    }

    @Override
    public E softUpdate(E entity) throws EntityNotFoundException {
        return decorated.softUpdate(entity);
    }

    @Override
    public E partialUpdate(E update, String... fieldsToUpdate) throws EntityNotFoundException {
        return decorated.partialUpdate(update,fieldsToUpdate);
    }

    @Override
    public E fullUpdate(E update) throws EntityNotFoundException {
        return decorated.fullUpdate(update);
    }

    @Override
    public E create(E entity) throws BadEntityException {
        return decorated.create(entity);
    }

    @Override
    public Set<E> findSome(Set<Id> ids) {
        return decorated.findSome(ids);
    }

    @Override
    public Set<E> findAll() {
        return decorated.findAll();
    }

    @Override
    public Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> entityFilters, List<SortingExtension> sortingStrategies) {
        return decorated.findAll(jpqlFilters,entityFilters,sortingStrategies);
    }

    @Override
    public void deleteById(Id id) throws EntityNotFoundException {
        decorated.deleteById(id);
    }

    @Override
    public void setBeanName(String name) {
        decorated.setBeanName(name);
    }

    public S getDecorated() {
        return decorated;
    }

}
