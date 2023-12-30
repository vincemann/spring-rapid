package com.github.vincemann.springrapid.core.util;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import java.util.Collection;

public class MyJpaUtils {


    public static <T> T deepDetach(EntityManager entityManager, T entity){
//        T unproxied = ProxyUtils.hibernateUnproxy(entity);
//        T detached;
//        boolean deepDetached = MyJpaUtils.isEntityDeepDetached(entityManager,unproxied);
//        if (!deepDetached)
//            detached = BeanUtils.clone(unproxied);
//        else
//            detached =  unproxied;
//        if (!MyJpaUtils.isEntityDeepDetached(entityManager,detached))
//            throw new IllegalArgumentException("deep detachment did not work");
        return BeanUtils.clone(ProxyUtils.hibernateUnproxy(entity));
    }

    public static boolean isEntityDeepDetached(EntityManager entityManager, Object entity) {
        if (entity == null) {
            return true; // Null objects are considered detached.
        }

        PersistenceUnitUtil unitUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();

        // Check if the entity itself is detached.
        boolean isDetached = !unitUtil.isLoaded(entity);

        // Recursively check sub-entities and collections.
        if (!isDetached) {
            // Iterate through fields of the entity.
            for (java.lang.reflect.Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                try {
                    Object fieldValue = field.get(entity);
                    if (fieldValue != null) {
                        if (fieldValue instanceof Collection) {
                            // Check if it's a collection and recursively verify its elements.
                            Collection<?> collection = (Collection<?>) fieldValue;
                            for (Object item : collection) {
                                if (!isEntityDeepDetached(entityManager,item)) {
                                    isDetached = false;
                                    break; // Exit the loop if any item is not detached.
                                }
                            }
                        } else {
                            // Check if it's a sub-entity and recursively verify it.
                            if (!isEntityDeepDetached(entityManager,fieldValue)) {
                                isDetached = false;
                                break; // Exit the loop if any sub-entity is not detached.
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Handle exception if needed.
                }
            }
        }

        return isDetached;
    }
}
