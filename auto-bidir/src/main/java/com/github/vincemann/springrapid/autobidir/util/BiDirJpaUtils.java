package com.github.vincemann.springrapid.autobidir.util;

import com.github.vincemann.springrapid.core.util.JpaUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.mapping.Collection;
import org.springframework.util.ReflectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

public class BiDirJpaUtils {

    /**
     * Used to initialize Lazy loaded (potentially not yet loaded) Entities, EntityCollection @toInitialize from @param entity.
     * Also merges @param entity if it is detached
     * @return
     */
    public static <T> T initializeSubEntity(T entity, Object toInitialize) {
        EntityManager entityManager = JpaUtils.getEntityManager();
        PersistenceUtil pu = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        System.out.println("entity is loaded: " + pu.isLoaded(entity));
        // find field Name of toInitialize
        AtomicReference<String> atomicFieldName = new AtomicReference<>(null);
        AtomicReference<Boolean> collectionFound = new AtomicReference<>(Boolean.FALSE);
        ReflectionUtils.doWithFields(entity.getClass(), field -> {
            if (atomicFieldName.get() != null){
                throw new IllegalArgumentException("Found multiple fields");
            }
            atomicFieldName.set(field.getName());
        }, field -> {
            if (field.getType().equals(toInitialize.getClass())){
                return true;
            }
            else if (Collection.class.isAssignableFrom(toInitialize.getClass()) && Collection.class.isAssignableFrom(field.getType())){
                // found collection field
                Class<?> entityType = EntityAnnotationUtils.getEntityType(field);
                if (entityType != null){
                    // found it
                    collectionFound.set(true);
                    return true;
                }
            }
            return false;
        });
        String fieldName = atomicFieldName.get();


        System.out.println("entity."+fieldName+" is loaded: " + pu.isLoaded(toInitialize, fieldName));
        if (!pu.isLoaded(entity) //entity might've been retrieved via getReference
                || !pu.isLoaded(entity, fieldName)//phones is a lazy relation
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
            System.out.println("initializing entity's field: " + fieldName + " by calling getter");
            try {
                Object returnedObj = PropertyUtils.getProperty(toInitialize, fieldName);
                if (collectionFound.get()){
                    System.out.println("initializing entity's collection field by calling size() on it");
                    Method sizeMethod = returnedObj.getClass().getDeclaredMethod("size");
                    sizeMethod.invoke(returnedObj);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalArgumentException("Could not call getter",e);
            }

//            //this will load/initialize employee entity
//            employee.getName();
//            employee.getDepartment();
//            //this will load lazy phones field
//            employee.getPhones().size();
//            entityManager.detach(employee);
//            //now employee is fully initialized
            System.out.println("entity initialized");
        }
        return entity;
    }
}
