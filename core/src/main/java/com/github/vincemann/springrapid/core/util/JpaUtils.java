package com.github.vincemann.springrapid.core.util;

import lombok.extern.slf4j.Slf4j;
import com.github.vincemann.springrapid.core.util.BeanUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUtil;
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

    private static Employee checkAndInitialize(Object entity, Object toInitialize) {
        PersistenceUtil pu = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        System.out.println("entity is loaded: " + pu.isLoaded(entity));
        System.out.println("entity.toInitialize is loaded: " + pu.isLoaded(toInitialize, "phones"));
        // müssen rausfinden was toInitialize für einen Field name in der entity.getClass() hat
        if (!pu.isLoaded(entity) //entity might've been retrieved via getReference
                || !pu.isLoaded(entity, "phones")//phones is a lazy relation
        ) {
            System.out.println("initializing entity");
            boolean detached = !entityManager.contains(entity);
            System.out.println("is entity detached: " + detached);
            if (detached) {
                System.out.println("merging entity");
                entity = entityManager.merge(entity);
            }

            // hier jetzt den getter callen von der property toInitialize
            // und falls es eine Collection ist, noch size callen


            //this will load/initialize employee entity
            employee.getName();
            employee.getDepartment();
            //this will load lazy phones field
            employee.getPhones().size();
            entityManager.detach(employee);
            //now employee is fully initialized
            System.out.println("employee initialized");
        }
        return employee;
    }

}
