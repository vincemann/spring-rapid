package com.github.vincemann.springrapid.autobidir.util;

import lombok.NonNull;
import org.springframework.data.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
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

    private static class NameAndAnnotationAndFieldTypeFilter extends AnnotationAndFieldTypeFilter {
        private Set<String> fieldNames = new HashSet<>();

        public NameAndAnnotationAndFieldTypeFilter(@NonNull Class<? extends Annotation> annotationType, Class<?> fieldType, Set<String> fieldNames) {
            super(annotationType, fieldType);
            this.fieldNames = fieldNames;
        }

        @Override
        public boolean matches(Field field) {
            if (fieldNames.isEmpty()){
                return super.matches(field);
            }else {
                return fieldNames.contains(field.getName()) && super.matches(field);
            }
        }
    }

    private static class AnnotationNamedFieldFilter extends ReflectionUtils.AnnotationFieldFilter {
        private Set<String> fieldNames = new HashSet<>();

        public AnnotationNamedFieldFilter(@NonNull Class<? extends Annotation> annotationType, Set<String> fieldNames) {
            super(annotationType);
            this.fieldNames = fieldNames;
        }

        @Override
        public boolean matches(Field field) {
            if (fieldNames.isEmpty()){
                return super.matches(field);
            }else {
                return fieldNames.contains(field.getName()) && super.matches(field);
            }
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

    public static void doWithNamedAnnotatedFieldsOfType(Class<?> fieldType, Class<? extends Annotation> annotationType, Class clazz,Set<String> membersToCheck, org.springframework.util.ReflectionUtils.FieldCallback fieldCallback){
        org.springframework.util.ReflectionUtils.doWithFields(clazz,field -> {
            org.springframework.util.ReflectionUtils.makeAccessible(field);
            fieldCallback.doWith(field);
        },new NameAndAnnotationAndFieldTypeFilter(annotationType,fieldType, membersToCheck));
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
