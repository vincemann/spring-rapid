package com.github.vincemann.springrapid.autobidir.util;

import com.github.vincemann.springrapid.core.util.JpaUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class AutoRelJpaUtils {



    /**
     * Used to initialize Lazy loaded (potentially not yet loaded) Entities, EntityCollection @toInitialize from
     * @param entity
     * Also merges
     * @param entity if it is detached
     *
     */
    public static <T> T initializeSubEntities(T entity, Class<? extends Annotation> annotationClass) {
        EntityManager entityManager = JpaUtils.getEntityManager();
        PersistenceUtil pu = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        System.err.println("entity is loaded: " + pu.isLoaded(entity));
        // find field Name of toInitialize
        final Set<String> fieldNames = new HashSet<>();
        boolean collection = EntityAnnotationUtils.isCollectionType(annotationClass);

        ReflectionUtils.doWithFields(entity.getClass(), field -> {
            fieldNames.add(field.getName());
        }, new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(annotationClass));

        if (fieldNames.isEmpty()) {
//            throw new IllegalArgumentException("Did not find matching property");
            return entity;
        }

        for (String fieldName : fieldNames) {
            System.err.println("entity." + fieldName + " is loaded: " + pu.isLoaded(entity, fieldName));
            if (!pu.isLoaded(entity) //entity might've been retrieved via getReference
                    || !pu.isLoaded(entity, fieldName)//phones is a lazy relation
            ) {
//                T merged = entity;
                System.err.println("initializing entity");
                boolean detached = !entityManager.contains(entity);
                System.err.println("is entity detached: " + detached);
                if (detached) {
                    System.err.println("merging entity");
//                     merged = entityManager.merge(entity);
                    entity = entityManager.merge(entity);
                }

                // hier jetzt den getter callen von der property toInitialize
                // und falls es eine Collection ist, noch size callen
                System.err.println("initializing entity's field: " + fieldName + " by calling getter");
                try {
//                    Object returnedObj = PropertyUtils.getProperty(merged, fieldName);
                    Object returnedObj = PropertyUtils.getProperty(entity, fieldName);
                    if (collection) {
                        System.err.println("initializing entity's collection field by calling size() on it");
                        Method sizeMethod = returnedObj.getClass().getDeclaredMethod("size");
                        sizeMethod.invoke(returnedObj);
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new IllegalArgumentException("Could not call getter", e);
                }

//            //this will load/initialize employee entity
//            employee.getName();
//            employee.getDepartment();
//            //this will load lazy phones field
//            employee.getPhones().size();
                if (detached) {
                    entityManager.detach(entity);
                }
//            //now employee is fully initialized
                System.err.println("entity initialized");
            }

        }


        return entity;
    }
}