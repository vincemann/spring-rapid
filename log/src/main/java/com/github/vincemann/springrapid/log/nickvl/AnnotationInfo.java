package com.github.vincemann.springrapid.log.nickvl;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;

@Getter
/**
 * Wrapper for Annotations including information about
 * where and in which context annotations were placed.
 */
class AnnotationInfo<A extends Annotation> extends ClassAnnotationInfo<A>{
    private boolean classLevel;

    protected AnnotationInfo(ClassAnnotationInfo<A> classAnnotationInfo){
        super(classAnnotationInfo.getAnnotation(),classAnnotationInfo.getTargetClass());
        this.classLevel=true;
    }

    @Builder(builderMethodName = "Builder",access = AccessLevel.PROTECTED)
    protected AnnotationInfo(A annotation, Class<?> targetClass, boolean classLevel) {
        super(annotation, targetClass);
        this.classLevel = classLevel;
    }
}
