package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.util.NullAwareBeanUtils;
import com.github.vincemann.springrapid.core.util.ReflectionUtils;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
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
    public Optional<E>findById(Id id) {
        if (id == null)
            throw new IllegalArgumentException("Id cannot be null");
        return getRepository().findById(id);
    }

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

    @Override
    public Class<?> getTargetClass() {
        return null;
    }

    @Transactional
    @Override
    public E partialUpdate(E update, Set<String> propertiesToUpdate, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        try {
            E entityToUpdate = findOldEntity(update.getId());
            // copy non null values from update to entityToUpdate
            // also copy null values from explicitly given fieldsToRemove
            // update is not yet applied, because setters ect are not called, which are hooked by hibernate
            NullAwareBeanUtils.copyProperties(entityToUpdate, update, propertiesToUpdate, Sets.newHashSet(fieldsToRemove));
            return getRepository().save(entityToUpdate);
        } catch (NonTransientDataAccessException e) {
            // constraints not met, such as foreign key constraints or other db entity constraints
            throw new BadEntityException(e);
        }
    }

    @Transactional
    @Override
    public E partialUpdate(E update, String... fieldsToRemove) throws EntityNotFoundException, BadEntityException {
        return partialUpdate(update,findNonRemovedUpdatedProperties(update),fieldsToRemove);
//        try {
//            E entityToUpdate = findOldEntity(update.getId());
//            // copy non null values from update to entityToUpdate
//            // also copy null values from explicitly given fieldsToRemove
//            // values get copied to target already bc this is transactional
//            // -> update on managed entity is already happening here
//            NullAwareBeanUtils.copyProperties(entityToUpdate, update, findNonRemovedUpdatedProperties(update), Sets.newHashSet(fieldsToRemove));
//            return getRepository().save(entityToUpdate);
//        } catch (NonTransientDataAccessException e) {
//            // constraints not met, such as foreign key constraints or other db entity constraints
//            throw new BadEntityException(e);
//        }
    }

    private Set<String> findNonRemovedUpdatedProperties(E partialUpdate){
        Set<String> nonNull = ReflectionUtils.findAllNonNullFieldNames(partialUpdate);
//        nonNull.addAll(Lists.newArrayList(fieldsToRemove));
        return nonNull;
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
//        return ServiceCallContextHolder.getContext().resolvePresentEntity(id,getEntityClass());
        Optional<E> entityToUpdate = findById(id);
        VerifyEntity.isPresent(entityToUpdate, id, getEntityClass());
        return entityToUpdate.get();
    }
}
