package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.util.NullAwareBeanUtilsBean;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


/**
 * Implementation of {@link AbstractCrudService} that utilizes Jpa's {@link JpaRepository}.
 *
 * @param <E>  Type of Entity which's crud operations are exposed by this Service
 * @param <Id> Id type of E
 * @param <R>  {@link JpaRepository} Type
 */
@ServiceComponent
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
    public Optional<E> findById(Id id) throws BadEntityException {
        VerifyEntity.notNull(id, "Id");
        try {
            return getRepository().findById(id);
        } catch (NonTransientDataAccessException e) {
            // constraints not met, such as foreign key constraints or other db entity constraints
            throw new BadEntityException(e);
        }
    }

    @Transactional
    @Override
    public E update(E update, Boolean full) throws EntityNotFoundException, BadEntityException {
        try {
            VerifyEntity.isPresent(update.getId(), "No Id set for update");
            if (full) {
                return getRepository().save(update);
            } else {
                E entityToUpdate = findOldEntity(update.getId());
                //copy non null values from update to entityToUpdate
                BeanUtilsBean notNull = new NullAwareBeanUtilsBean();
                notNull.copyProperties(entityToUpdate, update);
                return getRepository().save(entityToUpdate);
            }
        } catch (NonTransientDataAccessException e) {
            // constraints not met, such as foreign key constraints or other db entity constraints
            throw new BadEntityException(e);
        } catch (IllegalAccessException|InvocationTargetException e) {
           throw new RuntimeException(e);
        }
    }


    private E findOldEntity(Id id) throws BadEntityException, EntityNotFoundException {
        VerifyEntity.notNull(id, "id");
        Optional<E> entityToUpdate = findById(id);
        VerifyEntity.isPresent(entityToUpdate, id, getEntityClass());
        return entityToUpdate.get();
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


    @Transactional
    @Override
    public void deleteById(Id id) throws EntityNotFoundException, BadEntityException {
        try {
            VerifyEntity.notNull(id, "Id");
            Optional<E> entity = findById(id);
            VerifyEntity.isPresent(entity, id, getEntityClass());
            getRepository().deleteById(id);
        } catch (NonTransientDataAccessException e) {
            // constraints not met, such as foreign key constraints or other db entity constraints
            throw new BadEntityException(e);
        }

    }
}
