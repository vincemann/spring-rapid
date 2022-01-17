package com.github.vincemann.springrapid.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class ReflectionUtils {

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
    }
}
