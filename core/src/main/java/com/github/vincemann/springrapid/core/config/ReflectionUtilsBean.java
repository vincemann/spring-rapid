package com.github.vincemann.springrapid.core.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@Slf4j
public class ReflectionUtilsBean {

    private static ReflectionUtilsBean instance;

    public static ReflectionUtilsBean getInstance() {
        if (instance == null) {
            //mostly for tests
            log.warn("Using non Cached ReflectionUtilsBean, because instance not initialized by container, but already queried");
            instance = new ReflectionUtilsBean();
        }
        return instance;
    }

    static void initialize(ReflectionUtilsBean instance){
        //can only be called by AutoConfig to set aop cache proxied instance
        ReflectionUtilsBean.instance=instance;
    }

    @Cacheable(cacheNames = "reflections", cacheManager = "reflectionCacheManager")
    public Field[] getFieldsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
//        log.debug("get fields with annotations: " + cls.getSimpleName() + " ac: " + annotationCls.getSimpleName());
        return FieldUtils.getFieldsWithAnnotation(cls, annotationCls);
    }

}
