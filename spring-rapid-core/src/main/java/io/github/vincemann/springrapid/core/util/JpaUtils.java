package io.github.vincemann.springrapid.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.InvocationTargetException;

@Slf4j
public class JpaUtils {

    private static EntityManager entityManager;

    public JpaUtils(EntityManager entityManager) {
        JpaUtils.entityManager=entityManager;
    }

    public static  <T> T detach(T entity){
        if (entityManager==null){
            try {
                return (T) BeanUtilsBean.getInstance().cloneBean(entity);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }else {
            entityManager.detach(entity);
            return entity;
        }
    }

}
