package com.github.vincemann.springrapid.core.util;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtils {

    private static final Map<Class<?>, Set<String>> fieldNamesCache = new HashMap<>();


    public static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initializeNullCollectionFields(Object object) {



        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    Collection<?> fieldValue = (Collection<?>) field.get(object);

                    if (fieldValue == null) {
                        // Instantiate a HashSet for Set type, ArrayList for List, and ArrayList for other Collection types
                        if (Set.class.isAssignableFrom(field.getType())) {
                            field.set(object, new HashSet<>());
                        } else if (List.class.isAssignableFrom(field.getType())) {
                            field.set(object, new ArrayList<>());
                        } else {
                            // Default to ArrayList for other Collection types
                            field.set(object, new ArrayList<>());
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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

    private static Map<Class<?>, Set<Field>> entityCollectionFieldsCache = new ConcurrentHashMap<>();
    public static Set<Field> findEntityCollectionFields(Class<?> clazz){
        Set<Field> cachedResult = entityCollectionFieldsCache.get(clazz);
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

        entityCollectionFieldsCache.put(clazz, fields);

        return fields;
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
