package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.IBeanNameAware;
import com.github.vincemann.aoplog.api.annotation.LogConfig;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.util.Lists;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Interface for a service offering crud operations for entities of type E.
 * @param <E>       Type of entity
 * @param <Id>      Id type of entity
 */
@LogInteraction
@LogConfig(logAllChildrenMethods = true)
public interface CrudService<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AopLoggable, IBeanNameAware {

        Class<E> getEntityClass();

        Optional<E> findById(Id id);



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
        E partialUpdate(E entity, String... fieldsToUpdate) throws EntityNotFoundException, BadEntityException;

        E fullUpdate(E entity) throws BadEntityException, EntityNotFoundException;

        E create(E entity) throws BadEntityException;

        Set<E> findSome(Set<Id> ids);

        Set<E> findAll();

        // can always add own find methods using jpql or native sql to fetch entities instead of using this
        Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> filters, List<SortingExtension> sortingStrategies);

        default Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters, List<EntityFilter<? super E>> filters){
                return findAll(jpqlFilters,filters,new ArrayList<>());
        }

        default Set<E> findAll(List<QueryFilter<? super E>> jpqlFilters){
                return findAll(jpqlFilters,new ArrayList<>(),new ArrayList<>());
        }

        default Set<E> findAll(QueryFilter<? super E>... jpqlFilters){
                return findAll(Lists.newArrayList(jpqlFilters),new ArrayList<>(),new ArrayList<>());
        }

        default Set<E> findAll(SortingExtension... sortingStrategies){
                return findAll(new ArrayList<>(),new ArrayList<>(),Lists.newArrayList(sortingStrategies));
        }

        default Set<E> findAll(EntityFilter<? super E>... entityFilters){
                return findAll(new ArrayList<>(),Lists.newArrayList(entityFilters),new ArrayList<>());
        }


        void deleteById(Id id) throws EntityNotFoundException;
}
