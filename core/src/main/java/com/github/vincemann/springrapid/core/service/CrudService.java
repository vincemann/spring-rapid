package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.IBeanNameAware;
import com.github.vincemann.aoplog.api.annotation.LogConfig;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.AuditingEntity;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
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
         * only non null members of
         * @param entity will be taken into consideration for updating the database entity, except you want to remove
         *               certain solo fields (no collections), then use
         * @param fieldsToRemove for it.
         *
         *  Note: make sure to not accidentally have emtpy collections, that you dont want to update.
         *        Set the ignored collections to null or name fields to update explicitly
         */
        @Transactional
        E partialUpdate(E entity, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException;

        @Transactional
        E partialUpdate(E update, Set<String> propertiesToUpdate, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException;

        // the @Transactional's ara actually needed!
        @Transactional
        E fullUpdate(E entity) throws BadEntityException, EntityNotFoundException;

        @Transactional
        E save(E entity) throws BadEntityException;

        @Transactional
        Set<E> findAll();

        @Transactional
        Set<E> findSome(Set<Id> ids);

        // can always add own find methods using jpql or native sql to fetch entities instead of using this
        @Transactional
        Set<E> findAll(Set<EntityFilter<E>> filters);



        @Transactional
        void deleteById(Id id) throws EntityNotFoundException;
}
