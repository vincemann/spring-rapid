package com.github.vincemann.springrapid.core.util;

import lombok.NonNull;
import org.springframework.data.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


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

    public static Set<String> findCollectionFields(Set<String> fields, Class<?> entityClass) {
        Set<String> collectionFields = new HashSet<>();

        org.springframework.util.ReflectionUtils.doWithFields(entityClass, field -> {
            collectionFields.add(field.getName());
        }, field -> fields.contains(field.getName()) && Collection.class.isAssignableFrom(field.getType()));

        return collectionFields;
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

    private static class ReverseNameFilter implements org.springframework.util.ReflectionUtils.FieldFilter {
        private Set<String> fieldNames = new HashSet<>();

        public ReverseNameFilter(Set<String> fieldNames) {
            this.fieldNames = fieldNames;
        }

        @Override
        public boolean matches(Field field) {
            if (fieldNames.isEmpty()){
                return false;
            }else {
                return !fieldNames.contains(field.getName());
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


//    public static Set<Field> getReverseFields(Class clazz, Set<String> fields){
//        org.springframework.util.ReflectionUtils.doWithFields(o.getClass(),field -> {
//            field.setAccessible(true);
//            field.set(o,null);
//        });
//    }

    public static void setNonMatchingFieldsNull(Object o, Set<String> propertiesNotNull){
        org.springframework.util.ReflectionUtils.doWithFields(o.getClass(),field -> {
            if(com.github.vincemann.springrapid.core.util.ReflectionUtils.isStaticOrInnerField(field))
                return;
            if (field.getType().isPrimitive()){
                throw new IllegalArgumentException("entity must not contain primitive members, use Wrapper Types instead");
            }
            field.setAccessible(true);
            field.set(o,null);
        },new ReverseNameFilter(propertiesNotNull));
    }


}
