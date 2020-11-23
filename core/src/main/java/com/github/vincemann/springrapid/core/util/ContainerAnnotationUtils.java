package com.github.vincemann.springrapid.core.util;

import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.List;

public class ContainerAnnotationUtils {

    public static <A extends Annotation,C extends Annotation> List<A> findAnnotations(Class<?> clazz, Class<A> annotationClass, Class<C> containerAnnotationClass){
//        C containerAnnotation = clazz.getAnnotationsByType(annotationClass);
//        if (containerAnnotation==null){
//            A singleAnnotation = AnnotationUtils.findAnnotation(clazz, annotationClass);
//            if (singleAnnotation==null){
//                // none found
//                return Lists.newArrayList();
//            }
//            return Lists.newArrayList(singleAnnotation);
//        }
//        A[] annotations = (A[]) AnnotationUtils.getDefaultValue(containerAnnotation);
//        return Lists.newArrayList(annotations);
        A[] annotations = clazz.getAnnotationsByType(annotationClass);
        return Lists.newArrayList(annotations);
    }
}
