package com.github.vincemann.springrapid.log.nickvl;

import com.github.vincemann.springrapid.log.nickvl.AnnotationInfo;
import com.github.vincemann.springrapid.log.nickvl.ClassAnnotationInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


public interface AnnotationParser {
    public <A extends Annotation> AnnotationInfo<A> fromMethodOrClass(Method method, Class<A> type);
    public <A extends Annotation> A fromMethod(Method method,Class<A> type);
    public <A extends Annotation> ClassAnnotationInfo<A> fromClass(Class<?> clazz, Class<A> type);
}
