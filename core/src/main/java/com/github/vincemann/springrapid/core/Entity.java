package com.github.vincemann.springrapid.core;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public class Entity {

    /**
     * uses default constructor to create instance of this and nullifies all fields and sets id
     * -> make sure to always use this method to create instances used for {@link com.github.vincemann.springrapid.core.service.CrudService#partialUpdate(IdentifiableEntity, String...)}
     */
    public static <T extends IdentifiableEntity> T createUpdate(Class<T> clazz, Serializable id) {
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
            instance.setId(id);
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends IdentifiableEntity> T createUpdate(T entity) {
        return createUpdate(ProxyUtils.getTargetClass(entity),entity.getId());
    }
}
