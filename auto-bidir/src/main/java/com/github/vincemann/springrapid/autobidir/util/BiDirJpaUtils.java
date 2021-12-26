package com.github.vincemann.springrapid.autobidir.util;

import com.github.vincemann.springrapid.core.util.JpaUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class BiDirJpaUtils {

    /**
     * Used to initialize Lazy loaded (potentially not yet loaded) Entities, EntityCollection @toInitialize from @param entity.
     * Also merges @param entity if it is detached
     *
     * @return
     */
    public static <T> T initializeSubEntities(T entity, Object toInitialize, Class<? extends Annotation> annotationClass) {
        EntityManager entityManager = JpaUtils.getEntityManager();
        PersistenceUtil pu = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        System.err.println("entity is loaded: " + pu.isLoaded(entity));
        // find field Name of toInitialize
        final Set<String> fieldNames = new HashSet<>();
        AtomicReference<Boolean> collectionFound = new AtomicReference<>(Boolean.FALSE);
        AtomicReference<Boolean> collection = new AtomicReference<>(Collection.class.isAssignableFrom(toInitialize.getClass()));

        ReflectionUtils.doWithFields(entity.getClass(), field -> {
            fieldNames.add(field.getName());
        }, field -> {
            if (field.getType().equals(toInitialize.getClass())) {
                return true;
            } else if (collection.get() && Collection.class.isAssignableFrom(field.getType())) {
                // found collection field
                if (field.isAnnotationPresent(annotationClass)) {
                    collectionFound.set(true);
                    return true;
                }
//                Class<?> entityType = EntityAnnotationUtils.getEntityType(field);
//                if (entityType != null){
//                    if (entityType.equals(toInitialize.getClass())){
//                        // found it
//                        collectionFound.set(true);
//                        return true;
//                    }
//                }
            }
            return false;
        });
        if (fieldNames.isEmpty()) {
            throw new IllegalArgumentException("Did not find matching property");
        }

        for (String fieldName : fieldNames) {
            System.err.println("entity." + fieldName + " is loaded: " + pu.isLoaded(toInitialize, fieldName));
            if (!pu.isLoaded(entity) //entity might've been retrieved via getReference
                    || !pu.isLoaded(entity, fieldName)//phones is a lazy relation
            ) {
                System.err.println("initializing entity");
                boolean detached = !entityManager.contains(entity);
                System.err.println("is entity detached: " + detached);
                if (detached) {
                    System.err.println("merging entity");
                    entity = entityManager.merge(entity);
                }

                // hier jetzt den getter callen von der property toInitialize
                // und falls es eine Collection ist, noch size callen
                System.err.println("initializing entity's field: " + fieldName + " by calling getter");
                try {
                    Object returnedObj = PropertyUtils.getProperty(entity, fieldName);
                    if (collectionFound.get()) {
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
//            entityManager.detach(employee);
//            //now employee is fully initialized
                System.err.println("entity initialized");
            }

        }


        return entity;
    }
}
