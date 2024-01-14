package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;


/**
 * Implementation of {@link AbstractCrudService} that utilizes Jpa's {@link JpaRepository}.
 *
 * @param <E>  Type of Entity which's crud operations are exposed by this Service
 * @param <Id> Id type of E
 * @param <R>  {@link JpaRepository} Type
 */
@ServiceComponent
@Slf4j
public abstract class JPACrudService
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends JpaRepository<E, Id>
                >
        extends AbstractCrudService<E, Id, R> {


    public JPACrudService() {
    }

    @Transactional
    @Override
    public Optional<E> findById(Id id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null");
        return getRepository().findById(id);
    }

    @Transactional
    @Override
    public E softUpdate(E update) throws EntityNotFoundException, BadEntityException {
        VerifyEntity.isPresent(update.getId(), "No Id set for update");
        try {
            return getRepository().save(update);
        } catch (NonTransientDataAccessException e) {
            // constraints not met, such as foreign key constraints or other db update constraints
            throw new BadEntityException(e);
        }
    }

    @Transactional
    @Override
    public Set<E> findSome(Set<Id> ids) {
        return new HashSet<>(getRepository().findAllById(ids));
    }


    @Override
    public Class<?> getTargetClass() {
        return null;
    }

    @Transactional
    @Override
    public E partialUpdate(E update, Set<String> collectionsToUpdate, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        try {
//            E managedEntity = getRepository().findById(update.getId()).orElseThrow(() -> new EntityNotFoundException(update.getId(),getEntityClass()));
            E managedEntity = findOldEntity(update.getId());
//            try {
//                throw new IllegalArgumentException("test");
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            // never place breakpoint within this method, this triggers unexpected double execution of advices
            // always check logs to see if this method was executed twice
            // intellij idea says first breakpoint skipped because it happened in debugger evaluation, so the first execution never halts
            // the second invocation (that should never occur in the first place) halts then
//            System.err.println("managed entity: " + managedEntity);
            E detachedUpdateEntity = MyJpaUtils.deepDetachOrGet(update);
            // copy non null values from update to entityToUpdate
            // also copy null values from explicitly given fieldsToRemove
            // updates are applied on the go by hibernate bc entityToUpdate is managed in current session
//            System.err.println("start with copy properties");
            Set<String> whiteList = new HashSet<>(collectionsToUpdate);
            whiteList.addAll(Arrays.asList(fieldsToRemove));
            NullAwareBeanUtils.copyProperties(managedEntity, detachedUpdateEntity, whiteList);
//            System.err.println("start with repo call");
            return getRepository().save(managedEntity);
//            System.err.println("done with repo call");
//            return saved;
        } catch (NonTransientDataAccessException e) {
            // constraints not met, such as foreign key constraints or other db entity constraints
            throw new BadEntityException(e);
        }
    }

    @Transactional
    @Override
    public E partialUpdate(E update, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        return partialUpdate(update, findNonRemovedUpdatedProperties(update), fieldsToRemove);
    }

    private Set<String> findNonRemovedUpdatedProperties(E partialUpdate) {
        return ReflectionUtils.findAllNonNullFieldNames(partialUpdate);
    }


    @Transactional
    @Override
    public E fullUpdate(E update) throws BadEntityException, EntityNotFoundException {
        VerifyEntity.isPresent(update.getId(), "No Id set for update");
        try {
            return getRepository().save(update);
        } catch (NonTransientDataAccessException e) {
            // constraints not met, such as foreign key constraints or other db entity constraints
            throw new BadEntityException(e);
        }
    }


    @Transactional
    @Override
    public E save(E entity) throws BadEntityException {
        try {
            return getRepository().save(entity);
        } catch (NonTransientDataAccessException e) {
            // constraints not met, such as foreign key constraints or other db entity constraints
            throw new BadEntityException(e);
        }
    }

    @Transactional
    @Override
    public Set<E> findAll() {
        return new HashSet<>(getRepository().findAll());
    }

    /**
     * first jqpl filters applied (where clauses), then in memory filtering.
     * Can be combined as needed.
     */
    @Transactional
    @Override
    public Set<E> findAll(List<QueryFilter<E>> jpqlFilters, List<EntityFilter<E>> filters, List<EntitySortingStrategy<E>> sortingStrategies) {
        return applyMemoryFilters(new HashSet<>(getFilterRepository().findAll(jpqlFilters,sortingStrategies)), filters);
    }


    protected Set<E> applyMemoryFilters(Set<E> result, List<EntityFilter<E>> filters) {
        if (filters.isEmpty())
            return result;
        Set<E> filtered = new HashSet<>();
        for (E entity : result) {
            if (!isFilteredOut(filters,entity))
                filtered.add(entity);
        }
        return filtered;
    }

    /**
     * @return true if entity is filtered out -> not part of result set
     *         false if entity matches all filters -> is part of result set
     */
    protected boolean isFilteredOut(List<EntityFilter<E>> filters, E entity){
        for (EntityFilter<E> filter : filters) {
            if (log.isDebugEnabled())
                log.debug("applying memory filter: " + filter.getClass().getSimpleName());
            if (!filter.match(entity)) {
                if (log.isTraceEnabled())
                    log.trace("entity: " + entity + " did not match filter: " + filter);
                return true;
            }
        }
        return false;
    }


    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null");
        Optional<E> entity = findById(id);
        VerifyEntity.isPresent(entity, id, getEntityClass());
        getRepository().deleteById(id);
    }

    protected E findOldEntity(Id id) throws EntityNotFoundException {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null");
        return findById(id).orElseThrow(() -> new EntityNotFoundException(id, getEntityClass()));
    }
}
