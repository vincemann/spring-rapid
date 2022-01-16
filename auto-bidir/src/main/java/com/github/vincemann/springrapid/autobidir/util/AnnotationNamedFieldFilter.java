package com.github.vincemann.springrapid.autobidir.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class AnnotationNamedFieldFilter extends ReflectionUtils.AnnotationFieldFilter {
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
