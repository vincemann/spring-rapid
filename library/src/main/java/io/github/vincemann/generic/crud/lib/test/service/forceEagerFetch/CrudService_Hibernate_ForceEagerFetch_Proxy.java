package io.github.vincemann.generic.crud.lib.test.service.forceEagerFetch;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;
import lombok.Getter;
import org.apache.commons.collections4.MultiValuedMap;
import org.hibernate.Hibernate;
import org.hibernate.TransactionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static io.github.vincemann.generic.crud.lib.test.service.forceEagerFetch.CrudService_Hibernate_ForceEagerFetch_Proxy.EAGER_FETCH_PROXY;


/**
 * Proxy for {@link CrudService}, that forces eager fetching from database -> very slow, use only for testing
 * This is done by reflectively finding all {@link javax.persistence.Entity}s and EntityCollections and manually initializing them via {@link Hibernate#initialize(Object)}.
 * @param <E>
 * @param <Id>
 * @param <R>
 */
@Getter
@Qualifier(EAGER_FETCH_PROXY)
public class CrudService_Hibernate_ForceEagerFetch_Proxy
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E, Id>,
                S extends CrudService<E,Id,R>
                >
        implements CrudService<E, Id, R> {

    /**
     * use this in your service Beans as Qualifier String
     * example:
     *
     * @Qualifier(PROXY_QUALIFIER)
     * public interface MyServiceI {
     *     ...
     * }
     *
     * @Autowire @Qualifier(PROXY_QUALIFIER) MyServiceI myService
     *
     *
     */
    public static final String EAGER_FETCH_PROXY = "forceEagerFetchProxyBean";
    private S crudService;
    private PlatformTransactionManager transactionManager;

    public CrudService_Hibernate_ForceEagerFetch_Proxy(S crudService, PlatformTransactionManager transactionManager) {
        this.crudService = crudService;
        this.transactionManager = transactionManager;
    }


    protected  <Optional extends java.util.Optional> Optional runInTransactionAndFetchEagerly_OptionalValue(Callable<Optional> callable) throws Exception {
        TransactionStatus transactionStatus = startNewTransaction();
        try {
            Optional result = callable.call();
            result.ifPresent(this::eagerFetchAllEntities);
            getTransactionManager().commit(transactionStatus);
            return result;
        }catch (TransactionException e){
            //docs say we must not issue a rollback in this case
            throw e;
        }
        catch (Exception e){
            getTransactionManager().rollback(transactionStatus);
            throw e;
        }
    }

    protected  <Entity> Entity runInTransactionAndFetchEagerly(Callable<Entity> callable) throws Exception {
        TransactionStatus transactionStatus = startNewTransaction();
        try {
            Entity result = callable.call();
            eagerFetchAllEntities(result);
            getTransactionManager().commit(transactionStatus);
            return result;
        }catch (TransactionException e){
            //docs say we must not issue a rollback in this case
            throw e;
        }
        catch (Exception e){
            getTransactionManager().rollback(transactionStatus);
            throw e;
        }
    }

    @Override
    public java.util.Optional<E> findById(Id id) throws NoIdException {
        try {
            return runInTransactionAndFetchEagerly_OptionalValue(() -> crudService.findById(id));
        } catch (NoIdException e) {
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        /*
        TransactionStatus status = startNewTransaction();
        try {
            java.util.Optional<E> foundEntity = crudService.findById(id);
            foundEntity.ifPresent(this::eagerFetchAllEntities);
            transactionManager.commit(status);
            return foundEntity;
        } catch (NoIdException e) {
            transactionManager.rollback(status);
            throw e;
        }*/
    }

    private TransactionStatus startNewTransaction() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        return transactionManager.getTransaction(def);
    }

    @Override
    public E update(E entity) throws EntityNotFoundException, NoIdException, BadEntityException {
        try {
            return runInTransactionAndFetchEagerly(() -> {
                return crudService.update(entity);
            });
        } catch (EntityNotFoundException|NoIdException|BadEntityException e) {
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        /*
        TransactionStatus status = startNewTransaction();
        try {
            E updatedEntity = crudService.update(entity);
            eagerFetchAllEntities(updatedEntity);
            transactionManager.commit(status);
            return updatedEntity;
        } catch (EntityNotFoundException | NoIdException | BadEntityException e) {
            transactionManager.rollback(status);
            throw e;
        }*/
    }

    @Override
    public E save(E entity) throws BadEntityException {
        try {
            return runInTransactionAndFetchEagerly(() -> crudService.save(entity));
        } catch (BadEntityException e) {
            throw e;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        /*
        TransactionStatus status = startNewTransaction();
        try {
            E savedEntity = crudService.save(entity);
            eagerFetchAllEntities(savedEntity);
            transactionManager.commit(status);
            return savedEntity;
        } catch (BadEntityException e) {
            transactionManager.rollback(status);
            throw e;
        }*/
    }

    @Override
    public Set<E> findAll() {
        try {
            return runInTransactionAndFetchEagerly(() -> crudService.findAll());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        /*
        TransactionStatus status = startNewTransaction();
        Set<E> allEntities = crudService.findAll();
        for (E entity : allEntities) {
            eagerFetchAllEntities(entity);
        }
        transactionManager.commit(status);
        return allEntities;*/
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
            MultiValuedMap<Field, Object> field_instances_map = null;
            if(startEntity instanceof Collection){
                field_instances_map = ReflectionUtils.getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_AnnotatedWith(((Collection) startEntity), javax.persistence.Entity.class, true,true);
            }else {
                field_instances_map = ReflectionUtils.getAllFieldsAnnotatedWith_WithoutThisField_OfAllMemberVars_AnnotatedWith(startEntity, javax.persistence.Entity.class, true,true);
            }


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
