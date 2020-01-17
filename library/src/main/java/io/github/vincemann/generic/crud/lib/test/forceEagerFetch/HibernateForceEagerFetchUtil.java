package io.github.vincemann.generic.crud.lib.test.forceEagerFetch;

import io.github.vincemann.generic.crud.lib.util.ReflectionUtils;
import lombok.Getter;
import org.apache.commons.collections4.MultiValuedMap;
import org.hibernate.Hibernate;
import org.hibernate.TransactionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Callable;

@Component
@Getter
public class HibernateForceEagerFetchUtil {
    private PlatformTransactionManager transactionManager;

    @Autowired
    public HibernateForceEagerFetchUtil(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public  <Optional extends java.util.Optional> Optional runInTransactionAndFetchEagerly_OptionalValue(Callable<Optional> callable) throws Exception {
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

    public  <Optional extends java.util.Optional> Optional runInTransactionAndFetchEagerly_OptionalValue_NoException(Callable<Optional> callable) {
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
        catch (RuntimeException e){
            getTransactionManager().rollback(transactionStatus);
            throw e;
        }catch (Exception e){
            getTransactionManager().rollback(transactionStatus);
            throw new RuntimeException(e);
        }
    }

    public  <Entity> Entity runInTransactionAndFetchEagerly(Callable<Entity> callable) throws Exception {
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


    public  <Entity> Entity runInTransactionAndFetchEagerly_NoException(Callable<Entity> callable) {
        TransactionStatus transactionStatus = startNewTransaction();
        try {
            Entity result = callable.call();
            eagerFetchAllEntities(result);
            getTransactionManager().commit(transactionStatus);
            return result;
        }
        catch (TransactionException e){
            //docs say we must not issue a rollback in this case
            throw e;
        }
        catch (RuntimeException e){
            getTransactionManager().rollback(transactionStatus);
            throw e;
        }catch (Exception e){
            getTransactionManager().rollback(transactionStatus);
            throw new RuntimeException(e);
        }
    }

    public void eagerFetchAllEntities(Object startEntity) {
        try {
            MultiValuedMap<Field, Object> field_instances_map
                    = ReflectionUtils.findFieldsAndTheirDeclaringInstances_OfAllMemberVars_AnnotatedWith(startEntity, javax.persistence.Entity.class, true,true);
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

    private TransactionStatus startNewTransaction() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        return transactionManager.getTransaction(def);
    }
}
