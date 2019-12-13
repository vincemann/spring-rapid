package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;
import lombok.Getter;
import org.apache.commons.collections4.MultiValuedMap;
import org.hibernate.Hibernate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.Entity;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * Proxy for {@link CrudService}, that forces eager fetching from database -> very slow, use only for testing
 * This is done by reflectively finding all {@link Entity}s and EntityCollections and manually initializing them via {@link Hibernate#initialize(Object)}.
 * @param <E>
 * @param <Id>
 * @param <R>
 */
@Getter
public class Hibernate_ForceEagerFetch_CrudService_Proxy
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E, Id>,
                S extends CrudService<E,Id,R>
                >
        implements CrudService<E, Id, R> {

    private S crudService;
    private PlatformTransactionManager transactionManager;

    public Hibernate_ForceEagerFetch_CrudService_Proxy(S crudService, PlatformTransactionManager transactionManager) {
        this.crudService = crudService;
        this.transactionManager = transactionManager;
    }


    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        TransactionStatus status = startNewTransaction();
        try {
            Optional<E> foundEntity = crudService.findById(id);
            foundEntity.ifPresent(this::eagerFetchAllEntities);
            transactionManager.commit(status);
            return foundEntity;
        } catch (NoIdException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    protected TransactionStatus startNewTransaction() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        return transactionManager.getTransaction(def);
    }

    protected void commitTransaction(TransactionStatus transactionStatus){
        getTransactionManager().commit(transactionStatus);
    }

    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        TransactionStatus status = startNewTransaction();
        try {
            E updatedEntity = crudService.update(entity);
            eagerFetchAllEntities(updatedEntity);
            transactionManager.commit(status);
            return updatedEntity;
        } catch (EntityNotFoundException | NoIdException | BadEntityException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    @Override
    public E save(E entity) throws BadEntityException {
        TransactionStatus status = startNewTransaction();
        try {
            E savedEntity = crudService.save(entity);
            eagerFetchAllEntities(savedEntity);
            transactionManager.commit(status);
            return savedEntity;
        } catch (BadEntityException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    @Override
    public Set<E> findAll() {
        TransactionStatus status = startNewTransaction();
        Set<E> allEntities = crudService.findAll();
        for (E entity : allEntities) {
            eagerFetchAllEntities(entity);
        }
        transactionManager.commit(status);
        return allEntities;
    }

    @Override
    public void delete(E entity) throws EntityNotFoundException, NoIdException {
        crudService.delete(entity);
    }

    @Override
    public void deleteById(Id id) throws EntityNotFoundException, NoIdException {
        crudService.deleteById(id);
    }

    @Override
    public Class<E> getEntityClass() {
        return crudService.getEntityClass();
    }

    @Override
    public R getRepository() {
        return crudService.getRepository();
    }

    protected void eagerFetchAllEntities(Object startEntity) {
        try {
            MultiValuedMap<Field, Object> field_instances_map
                    = ReflectionUtils.getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_AnnotatedWith(startEntity, Entity.class, true,true);
            for (Map.Entry<Field, Object> entry : field_instances_map.entries()) {
                Field field = entry.getKey();
                Object instance = entry.getValue();
                field.setAccessible(true);
                //this is either a collection of entities or an entity-instance
                Object instanceThatNeedsToBeInitialized = field.get(instance);
                if(instanceThatNeedsToBeInitialized!=null){
                    Hibernate.initialize(instanceThatNeedsToBeInitialized);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
