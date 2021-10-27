package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.IBeanNameAware;
import com.github.vincemann.aoplog.api.LogConfig;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;


/**
 * Interface for a Service offering Crud Operations.
 * @param <E>       Type of Entity which's crud operations are exposed by this Service
 * @param <Id>      Id Type of E
 */
@ServiceComponent
@LogInteraction
@LogConfig(logAllChildrenMethods = true)
public interface CrudService<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AopLoggable, IBeanNameAware {

        Class<E> getEntityClass();

        @Transactional
        Optional<E> findById(Id id) throws BadEntityException;

        /**
         * If full is false:
         * only non null members of @param entity will be taken into consideration for updating the database entity.
         * If full is true:
         * all members of @param entity will be taken into consideration for updating the database entity.
         * @param entity
         * @return updated (database) entity
         */
        @Transactional
        E update(E entity, Boolean full) throws EntityNotFoundException, BadEntityException;

        // the @Transactional's ara actually needed!
        @Transactional
        default E update(E entity) throws BadEntityException, EntityNotFoundException {
                return update(entity,true);
        }

        @Transactional
        E save(E entity) throws  BadEntityException;

        @Transactional
        Set<E> findAll();

        @Transactional
        void deleteById(Id id) throws EntityNotFoundException, BadEntityException;
}
