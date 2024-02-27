package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.repo.FilterRepository;
import com.github.vincemann.springrapid.core.repo.FilterRepositoryImpl;
import com.github.vincemann.springrapid.core.repo.RapidJpaRepository;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.util.*;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.*;

import static com.github.vincemann.springrapid.core.util.FilterUtils.*;


/**
 * Implementation of {@link AbstractCrudService} that uses {@link JpaRepository}.
 *
 * @param <E>  Type of Entity whos crud operations are exposed by this Service
 * @param <Id> Id type of E
 * @param <R>  {@link JpaRepository} Type
 */

@Slf4j
public abstract class JpaCrudService
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends RapidJpaRepository<E, Id>
                >
        extends AbstractCrudService<E, Id, R>
        implements InitializingBean {

    protected FilterRepository<E, Id> filterRepository;
    protected EntityManager entityManager;

    public JpaCrudService() {
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<E> findById(Id id) {
        Assert.notNull(id,"id must not be null");
        return getRepository().findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public E findPresentById(Id id) throws EntityNotFoundException {
        Optional<E> byId = findById(id);
        VerifyEntity.isPresent(byId,id,getEntityClass());
        return byId.get();
    }

    @Transactional
    @Override
    public E softUpdate(E update) throws EntityNotFoundException {
        Assert.notNull(update.getId(),"id must not be null");
        return getRepository().save(update);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<E> findSome(Set<Id> ids) {
        return new HashSet<>(getRepository().findAllById(ids));
    }


    // be aware of when placing breakpoint within this method, this triggers unexpected double execution of advices
    // always check logs to see if this method was executed twice
    // intellij idea says first breakpoint skipped because it happened in debugger evaluation, so the first execution never halts
    // the second invocation (that should never occur in the first place) halts then
//            System.err.println("managed entity: " + managedEntity);
    @Transactional
    @Override
    public E partialUpdate(E update, String... fieldsToUpdate) throws EntityNotFoundException {
        Assert.notNull(update.getId(),"id must not be null");
        E managedEntity = findOldEntity(update.getId());
        E detachedUpdateEntity = JpaUtils.deepDetachOrGet(update);
        Set<String> propertiesToUpdate = Entity.findPartialUpdatedFields(update,fieldsToUpdate);
        NullAwareBeanUtils.copyProperties(managedEntity, detachedUpdateEntity, propertiesToUpdate);
        return getRepository().save(managedEntity);
    }


    @Transactional
    @Override
    public E fullUpdate(E update) throws EntityNotFoundException {
        Assert.notNull(update.getId(),"id must not be null");
        return getRepository().save(update);
    }


    @Transactional
    @Override
    public E create(E entity) throws BadEntityException {
        Assert.isNull(entity.getId(),"id must be null, dont use create method for update operations");
        return getRepository().save(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<E> findAll() {
        return new HashSet<>(getRepository().findAll());
    }

    /**
     * first query filters applied (where clauses), then in memory filtering.
     * Can be combined as needed.
     */
    @Transactional(readOnly = true)
    @Override
    public Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> filters, List<SortingExtension> sortingExtensions) {
        Set<E> result;
        if (sortingExtensions.isEmpty())
            result = new HashSet<>(filterRepository.findAll(Specification.where(toSpec(jpqlFilters))));
        else
            result = new LinkedHashSet<>(filterRepository.findAll(Specification.where(toSpec(jpqlFilters)), toSort(sortingExtensions)));
        return FilterUtils.applyMemoryFilters(result, filters);
    }


    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException {
        Assert.notNull(id,"id must not be null");
        Optional<E> entity = findById(id);
        VerifyEntity.isPresent(entity, id, getEntityClass());
        getRepository().deleteById(id);
    }

    protected E findOldEntity(Id id) throws EntityNotFoundException {
        Assert.notNull(id,"id must not be null");
        return findById(id).orElseThrow(() -> new EntityNotFoundException(id, getEntityClass()));
    }

    // todo need better bean based solution
    // create own default jpaRepository containing methods (put into JpaRapidRepo)
    // in sync or softdelete create own Default Repos
    @LogInteraction(disabled = true)
    @Override
    public void afterPropertiesSet() throws Exception {
        this.filterRepository = new FilterRepositoryImpl<>(getEntityClass(), entityManager);
    }


//    @Autowired
//    public void setFilterRepository(FilterRepository<E, Id> filterRepository) {
//        this.filterRepository = filterRepository;
//    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
