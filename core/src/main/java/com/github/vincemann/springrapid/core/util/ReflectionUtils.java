package com.github.vincemann.springrapid.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
        return result;
    }

    public static boolean isStaticOrInnerField(Field field){
        return field.getName().indexOf('$') != -1 || Modifier.isStatic(field.getModifiers());
    }

    private static Map<Class<?>, Set<Field>> collectionFieldsCache = new ConcurrentHashMap<>();
    public static Set<Field> findCollectionFields(Class<?> clazz){
        Set<Field> cachedResult = collectionFieldsCache.get(clazz);
        if (cachedResult != null) {
            return cachedResult;
        }

        Set<Field> fields = new HashSet<>();
        org.springframework.util.ReflectionUtils.doWithFields(clazz, fields::add,
                field -> {
            // dont also check for these annotations, it is required to also check for collections that do not represent other entities
//                    Annotation[] annotations = field.getDeclaredAnnotations();
//                    boolean annotationMatch = Arrays.stream(annotations).sequential().anyMatch(a -> a.annotationType().equals(OneToMany.class) || a.annotationType().equals(ManyToMany.class));
//                    if (!annotationMatch)
//                        return false;
//                    else
                    return Collection.class.isAssignableFrom(field.getType());
                });

        collectionFieldsCache.put(clazz, fields);

        return fields;
    }

    public static Set<Field> findFieldsAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotationClass){
        Set<Field> cachedResult = collectionFieldsCache.get(clazz);
        if (cachedResult != null) {
            return cachedResult;
        }

        Set<Field> fields = new HashSet<>();
       EntityReflectionUtils.doWithAnnotatedFields(annotationClass,clazz, fields::add);

        collectionFieldsCache.put(clazz, fields);

        return fields;
    }

    public static Set<String> findAllNonNullFieldNames(Object entity){

        Set<String> result = new HashSet<>();
        org.springframework.util.ReflectionUtils.doWithFields(entity.getClass(), f -> {
            result.add(f.getName());
        }, field -> {
            if (isStaticOrInnerField(field)){
                return false;
            }
            field.setAccessible(true);
            return org.springframework.util.ReflectionUtils.getField(field,entity) != null;
        });

        return result;
    }
}
