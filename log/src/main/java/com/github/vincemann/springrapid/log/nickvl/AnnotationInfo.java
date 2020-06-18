package com.github.vincemann.springrapid.log.nickvl;

import com.github.vincemann.springrapid.log.nickvl.annotation.LogConfig;
import com.github.vincemann.springrapid.log.nickvl.annotation.Logging;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Builder
@AllArgsConstructor
@Getter
/**
 * Wrapper for Annotations including information about
 * where and in which context annotations were placed.
 */
public class AnnotationInfo<A extends Annotation> {
    private A annotation;
    private boolean classLevel;
    private Class<?> targetClass;

    public AnnotationInfo(ClassAnnotationInfo<A> classAnnotationInfo){
        this.annotation=classAnnotationInfo.getAnnotation();
        this.targetClass=classAnnotationInfo.getTargetClass();
        this.classLevel=true;
    }
}
