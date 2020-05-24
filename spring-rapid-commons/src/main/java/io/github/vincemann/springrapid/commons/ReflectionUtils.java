package io.github.vincemann.springrapid.commons;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class ReflectionUtils {



    public static Set<String> getProperties(Class c) {
        return getFields(c).stream()
                .map(Field::getName)
                .collect(Collectors.toSet());
    }



    /**
     * Gets all fields, also from superclasses.
     * Ignores Static and "this" fields.
     */
    public static Set<Field> getFields(Class clazz) {
        return Arrays.stream(FieldUtils.getAllFields(clazz))
                .filter(f -> !Modifier.isStatic(f.getModifiers()) && !f.getName().startsWith("this$"))
                .collect(Collectors.toSet());
    }
    /**
     * Ignores static fields.
     *
     * @param clazz
     * @return
     */
    public static Map<String, Field> getNameFieldMap(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field entityField : getFields(clazz)) {
            fieldMap.put(entityField.getName(), entityField);
        }
        return fieldMap;
    }

}