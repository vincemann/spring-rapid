package com.github.vincemann.springrapid.core.util;

import lombok.extern.slf4j.Slf4j;
import com.github.vincemann.springrapid.core.util.BeanUtils;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.mapping.Collection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class JpaUtils {

    private static EntityManager entityManager;

    public static void setEntityManager(EntityManager entityManager) {
        JpaUtils.entityManager = entityManager;
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }


    public static boolean isManaged(Object entity){
        EntityManager entityManager = JpaUtils.getEntityManager();
        PersistenceUtil pu = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        return pu.isLoaded(entity);
    }
    public static boolean isManaged(Object entity, String member){
        EntityManager entityManager = JpaUtils.getEntityManager();
        PersistenceUtil pu = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        boolean entityManaged = pu.isLoaded(entity);
        boolean memberManaged = pu.isLoaded(entity, member);
        return entityManaged && memberManaged;
    }

    public static void detachCollections(Object entity) {
        // Get all fields of the entity class, including private fields
        try {
            for (Field collectionField : ReflectionUtils.findEntityCollectionFields(entity.getClass())) {
                collectionField.setAccessible(true);
                // Check if the field is a PersistentSet collection
                Object collection = collectionField.get(entity);
                if (collection == null)
                    continue;
                if (collection instanceof PersistentSet) {
                    PersistentSet persistentSet = (PersistentSet) collection;

                    // Initialize the collection to ensure it's loaded
//                    Hibernate.initialize(persistentSet);

                    // Replace with a detached HashSet
                    Set<?> detachedSet = new HashSet<>(persistentSet);

                    collectionField.set(entity, detachedSet);

                }
            }
        }catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T detach(T entity) {
        if (entityManager == null) {
            log.warn("Entity Manager is null. Cloning entity instead of detaching.");
            return BeanUtils.clone(entity);
        } else {
            entityManager.detach(entity);
//            while (entityManager.contains(entity)){
//                try {
//                    Thread.sleep(5);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
            return entity;
        }
    }



}
