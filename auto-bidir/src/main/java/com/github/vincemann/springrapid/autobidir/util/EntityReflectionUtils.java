package com.github.vincemann.springrapid.autobidir.util;

import lombok.NonNull;
import org.springframework.data.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;

import static com.github.vincemann.springrapid.autobidir.util.EntityIdAnnotationUtils.getEntityType;

public class EntityReflectionUtils {

    private static class AnnotationAndFieldTypeFilter extends org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter {
        private final Class<?> fieldType;

        public AnnotationAndFieldTypeFilter(@NonNull Class<? extends Annotation> annotationType, Class<?> fieldType) {
            super(annotationType);
            this.fieldType = fieldType;
        }

        @Override
        public boolean matches(Field field) {
            return super.matches(field) && field.getType().equals(fieldType);
        }
    }

    private static class AnnotationAndEntityTypeFilter extends org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter {
        private Class<? extends Annotation> annotationType;
        private final Class<?> entityType;

        public AnnotationAndEntityTypeFilter(@NonNull Class<? extends Annotation> annotationType, Class<?> entityType) {
            super(annotationType);
            this.annotationType = annotationType;
            this.entityType = entityType;
        }

        @Override
        public boolean matches(Field field) {
            return super.matches(field) && getEntityType(field.getAnnotation(annotationType)).equals(entityType);
        }
    }

    public static void doWithAnnotatedFields(Class<? extends Annotation> annotationType, Class clazz, org.springframework.util.ReflectionUtils.FieldCallback fieldCallback){
        org.springframework.util.ReflectionUtils.doWithFields(clazz,field -> {
            org.springframework.util.ReflectionUtils.makeAccessible(field);
            fieldCallback.doWith(field);
        },new ReflectionUtils.AnnotationFieldFilter(annotationType));
    }

    public static void doWithAnnotatedNamedFields(Class<? extends Annotation> annotationType, Class clazz, Set<String> names, org.springframework.util.ReflectionUtils.FieldCallback fieldCallback){
        org.springframework.util.ReflectionUtils.doWithFields(clazz,field -> {
            org.springframework.util.ReflectionUtils.makeAccessible(field);
            fieldCallback.doWith(field);
        },new AnnotationNamedFieldFilter(annotationType,names));
    }


    public static void doWithAnnotatedFieldsOfType(Class<?> fieldType, Class<? extends Annotation> annotationType, Class clazz, org.springframework.util.ReflectionUtils.FieldCallback fieldCallback){
        org.springframework.util.ReflectionUtils.doWithFields(clazz,field -> {
            org.springframework.util.ReflectionUtils.makeAccessible(field);
            fieldCallback.doWith(field);
        },new AnnotationAndFieldTypeFilter(annotationType,fieldType));
    }

    /**
     * Offers callback for fields in @param class that have annotation of type @param annotationType.
     * Annotation Type must be of EntityIdType, see: {@link EntityIdAnnotationUtils#getEntityType(Annotation)}.
     * The Value of this Annotation, which is always the entityType must match @param entityType
     * @param clazz
     * @param fieldCallback
     * @param annotationType
     * @param entityType
     */
    public static void doWithIdFieldsWithEntityType(Class<?> entityType, Class<? extends Annotation> annotationType, Class clazz, org.springframework.util.ReflectionUtils.FieldCallback fieldCallback){
        org.springframework.util.ReflectionUtils.doWithFields(clazz,field -> {
            org.springframework.util.ReflectionUtils.makeAccessible(field);
            fieldCallback.doWith(field);
        },new AnnotationAndEntityTypeFilter(annotationType,entityType));
    }



}
