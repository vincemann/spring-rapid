package com.github.vincemann.springrapid.log.nickvl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;

@Builder
@AllArgsConstructor
@Getter
public class ClassAnnotationInfo<A extends Annotation> {
    private A annotation;
    private Class<?> targetClass;
}
