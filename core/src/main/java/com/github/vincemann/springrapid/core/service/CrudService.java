package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.IBeanNameAware;
import com.github.vincemann.aoplog.api.annotation.LogConfig;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.EntitySortingStrategy;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.util.Lists;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Interface for a Service offering Crud Operations.
 * @param <E>       Type of Entity whos crud operations are exposed by this Service
 * @param <Id>      Id Type of E
 */
@ServiceComponent
@LogInteraction
@LogConfig(logAllChildrenMethods = true)
public interface CrudService<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AopLoggable, IBeanNameAware {

        Class<E> getEntityClass();

        @Transactional
        Optional<E> findById(Id id);



        @Transactional
        /**
         * Expects that no entity relationships need to be updated by other framework logic.
         * Use this i.E. if you just update a long field and String field of entity, to reduce overhead.
         */
        E softUpdate(E entity) throws EntityNotFoundException, BadEntityException;
        /**
         * Only fieldsToUpdate are updated. When not supplied, all non null fields of entity are updated.
         * Collections need to always be explicitly listed in fieldsToUpdate.
         * Same for fields that are null in entity and should be null in target.
         *
         *  Note: make sure to not accidentally have emtpy collections, that you dont want to update.
         *        Set the ignored collections to null or name fields to update explicitly
         */
        @Transactional
        E partialUpdate(E entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException;

        // the @Transactional's ara actually needed!
        @Transactional
        E fullUpdate(E entity) throws BadEntityException, EntityNotFoundException;

        @Transactional
        E save(E entity) throws BadEntityException;

        @Transactional
        Set<E> findSome(Set<Id> ids);

        @Transactional
        Set<E> findAll();

        // can always add own find methods using jpql or native sql to fetch entities instead of using this
        @Transactional
        Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> filters, List<EntitySortingStrategy<? super E>> sortingStrategies);

        @Transactional
        default Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> filters){
                return findAll(jpqlFilters,filters,new ArrayList<>());
        }

        @Transactional
        default Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters){
                return findAll(jpqlFilters,new ArrayList<>(),new ArrayList<>());
        }

        @Transactional
        default Set<E> findAll(QueryFilter<? super E>... jpqlFilters){
                return findAll(Lists.newArrayList(jpqlFilters),new ArrayList<>(),new ArrayList<>());
        }

        @Transactional
        default Set<E> findAll(EntitySortingStrategy<? super E>... sortingStrategies){
                return findAll(new ArrayList<>(),new ArrayList<>(),Lists.newArrayList(sortingStrategies));
        }

        @Transactional
        default Set<E> findAll(EntityFilter<? super E>... entityFilters){
                return findAll(new ArrayList<>(),Lists.newArrayList(entityFilters),new ArrayList<>());
        }


        @Transactional
        void deleteById(Id id) throws EntityNotFoundException;
}
