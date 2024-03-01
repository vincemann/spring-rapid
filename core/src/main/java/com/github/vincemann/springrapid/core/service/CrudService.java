package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;
import com.github.vincemann.springrapid.core.util.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Interface for service offering crud operations for one entity type
 * @param <E>       Type of entity
 * @param <Id>      Id type of entity
 */
public interface CrudService<E extends IdentifiableEntity<Id>,Id extends Serializable>
{

        Class<E> getEntityClass();

        Optional<E> findById(Id id);

        E findPresentById(Id id) throws EntityNotFoundException;


        /**
         * Only use for properties that do not involve other entities.
         * All properties must be set.
         *
         * @param entity update entity having updated fields and not updated fields set
         * @return updated entity
         * @throws EntityNotFoundException
         */
        E softUpdate(E entity) throws EntityNotFoundException;

        /**
         * Only {@param fieldsToUpdate} are updated. When not supplied, all non {@code null} fields of entity are updated.
         * Updated collections need to always be explicitly listed in {@param fieldsToUpdate}.
         * Same for fields that should be set to {@code null} in target.
         *
         * Note:
         * If you dont supply {@param fieldsToUpdate}, make sure to not accidentally have emtpy collections set in {@param update}, that you don't want to update.
         * Set the ignored collections to null or name fields to update explicitly.
         * It best to use {@link com.github.vincemann.springrapid.core.util.Entity#createUpdate(IdentifiableEntity)} to create {@param update} entity
         * @param update update entity, containing all fields that should be updated
         * @param fieldsToUpdate all field names that should be updated. Optional if <strong>not</strong> setting values to null or updating collections
         * @return updated entity
         * @throws EntityNotFoundException
         */
        E partialUpdate(E update, String... fieldsToUpdate) throws EntityNotFoundException;


        E fullUpdate(E update) throws EntityNotFoundException;

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
