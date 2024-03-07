package com.github.vincemann.springrapid.core.util;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class JpaUtils {


    public static <T> T deepDetach(T entity){
        return clone(HibernateProxyUtils.unproxy(entity));
    }

    private static <T> T clone(T bean){
        try {
            return (T) BeanUtilsBean.getInstance().cloneBean(bean);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deepDetachOrGet(T entity){
        boolean deepDetached = JpaUtils.isEntityDeepDetached(entity);
        if (!deepDetached){
            return JpaUtils.deepDetach(entity);
        }
        else
            return entity;
    }

    /**
     * looks for hibernate proxy and PersistentCollection types, and if cannot find any, the entity must be deep detached
     */
    public static boolean isEntityDeepDetached(Object entity) {
        if (entity == null) {
            return true; // Null objects are considered detached.
        }

//        PersistenceUnitUtil unitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        // Check if the entity itself is detached.

        boolean isDetached = !(entity instanceof HibernateProxy);
        // If the entity is not detached, check its associations.
        if (!isDetached) {
            Class<?> entityClass = entity.getClass();
            Field[] fields = entityClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                try {
                    Object fieldValue = field.get(entity);

                    if (fieldValue != null) {
                        if (fieldValue instanceof PersistentCollection || fieldValue instanceof HibernateProxy) {
                            // It's a collection or a proxy, so it's attached.
                            isDetached = false;
                            break; // Exit the loop if any attached association is found.
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Handle exception if needed.
                }
            }
        }

        return isDetached;
    }

    //    private static EntityManager entityManager;
//
//    public static void setEntityManager(EntityManager entityManager) {
//        JpaUtils.entityManager = entityManager;
//    }
//
//    public static EntityManager getEntityManager() {
//        return entityManager;
//    }
//
//
//    public static boolean isManaged(Object entity){
//        EntityManager entityManager = JpaUtils.getEntityManager();
//        PersistenceUtil pu = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
//        return pu.isLoaded(entity);
//    }
//    public static boolean isManaged(Object entity, String member){
//        EntityManager entityManager = JpaUtils.getEntityManager();
//        PersistenceUtil pu = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
//        boolean entityManaged = pu.isLoaded(entity);
//        boolean memberManaged = pu.isLoaded(entity, member);
//        return entityManaged && memberManaged;
//    }
//
//    public static void detachCollections(Object entity) {
//        // Get all fields of the entity class, including private fields
//        try {
//            for (Field collectionField : ReflectionUtils.findEntityCollectionFields(entity.getClass())) {
//                collectionField.setAccessible(true);
//                // Check if the field is a PersistentSet collection
//                Object collection = collectionField.get(entity);
//                if (collection == null)
//                    continue;
//                if (collection instanceof PersistentSet) {
//                    PersistentSet persistentSet = (PersistentSet) collection;
//
//                    // Initialize the collection to ensure it's loaded
////                    Hibernate.initialize(persistentSet);
//
//                    // Replace with a detached HashSet
//                    Set<?> detachedSet = new HashSet<>(persistentSet);
//
//                    collectionField.set(entity, detachedSet);
//
//                }
//            }
//        }catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static <T> T detach(T entity) {
//        if (entityManager == null) {
//            log.warn("Entity Manager is null. Cloning entity instead of detaching.");
//            return BeanUtils.clone(entity);
//        } else {
//            entityManager.detach(entity);
////            while (entityManager.contains(entity)){
////                try {
////                    Thread.sleep(5);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////            }
//            return entity;
//        }
//    }
//
//
//
}
