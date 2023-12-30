package com.github.vincemann.springrapid.core.util;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import java.lang.reflect.Field;
import java.util.Collection;

@Slf4j
public class MyJpaUtils {


    public static <T> T deepDetach(T entity){
        return BeanUtils.clone(ProxyUtils.hibernateUnproxy(entity));
    }

    public static <T> T deepDetachOrGet(T entity){
        boolean deepDetached = MyJpaUtils.isEntityDeepDetached(entity);
        if (!deepDetached){
            if (log.isWarnEnabled())
                log.warn("entity is not deep detached - deep detaching...");
            return MyJpaUtils.deepDetach(entity);
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
}
