package com.github.vincemann.springrapid.autobidir.util;

import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class RelationalEntityReflectionUtils {

    private static class AnnotationAndEntityTypeFilter extends org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter {
        private Class<? extends Annotation> annotationType;
        private final Class<?> entityType;

        public AnnotationAndEntityTypeFilter(Class<? extends Annotation> annotationType, Class<?> entityType) {
            super(annotationType);
            this.annotationType = annotationType;
            this.entityType = entityType;
        }

        @Override
        public boolean matches(Field field) {
            return super.matches(field) && RelationalEntityIdAnnotationUtils.getEntityType(field.getAnnotation(annotationType)).equals(entityType);
        }
    }

    /**
     * Offers callback for fields in @param class that have annotation of type @param annotationType.
     * Annotation Type must be of EntityIdType, see: {@link RelationalEntityIdAnnotationUtils#getEntityType(Annotation)}.
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
