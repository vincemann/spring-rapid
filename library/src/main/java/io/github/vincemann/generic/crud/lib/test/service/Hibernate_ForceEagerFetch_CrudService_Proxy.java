package io.github.vincemann.generic.crud.lib.test.service;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;
import org.hibernate.Hibernate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;


public class Hibernate_ForceEagerFetch_CrudService_Proxy
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E, Id>
                >
        implements CrudService<E, Id, R> {

    private CrudService<E, Id, R> crudService;
    private PlatformTransactionManager transactionManager;

    public Hibernate_ForceEagerFetch_CrudService_Proxy(CrudService<E, Id, R> crudService, PlatformTransactionManager transactionManager) {
        this.crudService = crudService;
        this.transactionManager = transactionManager;
    }


    @Override
    public Optional<E> findById(Id id) throws NoIdException {
        TransactionStatus status = startNewTransaction();
        try {
            Optional<E> foundEntity = crudService.findById(id);
            foundEntity.ifPresent(this::initializeAllCollections);
            transactionManager.commit(status);
            return foundEntity;
        } catch (NoIdException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    private TransactionStatus startNewTransaction() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        return transactionManager.getTransaction(def);
    }

    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        TransactionStatus status = startNewTransaction();
        try {
            E updatedEntity = crudService.update(entity);
            initializeAllCollections(updatedEntity);
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
            initializeAllCollections(savedEntity);
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
            initializeAllCollections(entity);
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

    private void initializeAllCollections(Object entity) {
        try {
            Set<Field> entityGraphFields = ReflectionUtils.getAllFields_WithoutThisField_OfAllMemberVars_AnnotatedWith(entity, Entity.class,true);
            for (Field relevantField : entityGraphFields) {
                if(relevantField.isAnnotationPresent(OneToMany.class)){
                    relevantField.setAccessible(true);
                    Object collection = relevantField.get(entity);
                    if(collection!=null) {
                        Hibernate.initialize(collection);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
