package com.github.vincemann.springrapid.core.util;

import lombok.extern.slf4j.Slf4j;
import com.github.vincemann.springrapid.core.util.BeanUtils;
import org.hibernate.mapping.Collection;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class JpaUtils {

    private static EntityManager entityManager;

    public JpaUtils(EntityManager entityManager) {
        JpaUtils.entityManager = entityManager;
    }

    public static EntityManager getEntityManager() {
        return entityManager;
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
