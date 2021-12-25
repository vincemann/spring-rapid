package com.github.vincemann.springrapid.core.util;

import lombok.extern.slf4j.Slf4j;
import com.github.vincemann.springrapid.core.util.BeanUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class JpaUtils {

    private static EntityManager entityManager;

    public JpaUtils(EntityManager entityManager) {
        JpaUtils.entityManager = entityManager;
    }

    public static <T> T detach(T entity) {
        if (entityManager == null) {
            log.warn("Entity Manager is null. Cloning entity instead of detaching.");
            return BeanUtils.clone(entity);
        } else {
            entityManager.detach(entity);
            return entity;
        }
    }

}
