package com.github.vincemann.springrapid.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtils {

    private static final Map<Class<?>, Set<String>> fieldNamesCache = new HashMap<>();

    public static void setFinal(Field field, Object target, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(target, newValue);
    }

    public static Set<String> findAllFieldNamesExcept(Set<String> except, Class clazz){
        Set<String> result = new HashSet<>();
        org.springframework.util.ReflectionUtils.doWithFields(clazz, f -> {
            result.add(f.getName());
        }, new org.springframework.util.ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                return !except.contains(field.getName());
            }
        });
        return result;
    }

    public static boolean isStaticOrInnerField(Field field){
        return field.getName().indexOf('$') != -1 || Modifier.isStatic(field.getModifiers());
    }

    public static Set<String> findAllNonNullFieldNames(Object entity){

        Class<?> entityClass = entity.getClass();

        // Check if the result is already cached
        if (fieldNamesCache.containsKey(entityClass)) {
            return fieldNamesCache.get(entityClass);
        }

        Set<String> result = new HashSet<>();
        org.springframework.util.ReflectionUtils.doWithFields(entity.getClass(), f -> {
            result.add(f.getName());
        }, new org.springframework.util.ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                if (isStaticOrInnerField(field)){
                    return false;
                }
                field.setAccessible(true);
                return org.springframework.util.ReflectionUtils.getField(field,entity) != null;
            }
        });


        // Cache the result for this class
        fieldNamesCache.put(entityClass, result);

        return result;
    }
}
