package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.google.common.collect.Sets;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Set;

public class Entity {

    /**
     * uses default constructor to create instance of this and nullifies all fields and sets id
     * -> make sure to always use this method to create instances used for {@link com.github.vincemann.springrapid.core.service.CrudService#partialUpdate(IdentifiableEntity, String...)}
     */
    public static <T extends IdentifiableEntity> T createUpdate(Class<T> clazz, Serializable id) {
        T instance = createUpdate(clazz);
        instance.setId(id);
        return instance;
    }

    public static <T extends IdentifiableEntity> T createUpdate(Class<T> clazz) {
//        Class<?> clazz = this.getClass();
        // Create an instance of the class using Spring's ReflectionUtils

        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            // Use Spring's ReflectionUtils to iterate through fields
            ReflectionUtils.doWithFields(clazz, field -> {
                if (!Modifier.isStatic(field.getModifiers())){
                    field.setAccessible(true); // Make the field accessible
                    field.set(instance, null);
                }
            });
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends IdentifiableEntity> T createUpdate(T entity) {
        return createUpdate(HibernateProxyUtils.getTargetClass(entity),entity.getId());
    }

    public static Set<String> findPartialUpdatedFields(IdentifiableEntity update, String... fieldsToUpdate){
        if (fieldsToUpdate.length == 0)
            return com.github.vincemann.springrapid.core.util.ReflectionUtils.findAllNonNullFieldNames(update);
        else
            return Sets.newHashSet(fieldsToUpdate);
    }
}
