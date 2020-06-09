package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.commons.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

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
    public Set<String> getProperties(Class c) {
        return ReflectionUtils.getProperties(c);
    }

    @Cacheable(cacheNames = "reflections", cacheManager = "reflectionCacheManager")
    public Set<Field> getFields(Class clazz) {
        return ReflectionUtils.getFields(clazz);
    }

    @Cacheable(cacheNames = "reflections", cacheManager = "reflectionCacheManager")
    public Map<String, Field> getNameFieldMap(Class<?> clazz) {
        return ReflectionUtils.getNameFieldMap(clazz);
    }

    @Cacheable(cacheNames = "reflections", cacheManager = "reflectionCacheManager")
    public Field[] getFieldsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
        log.debug("get fields with annotations: " + cls.getSimpleName() + " ac: " + annotationCls.getSimpleName());
        return FieldUtils.getFieldsWithAnnotation(cls, annotationCls);
    }

    @Cacheable("reflections")
    public Field getField(final Class<?> cls, final String fieldName, final boolean forceAccess) {
        return FieldUtils.getField(cls, fieldName, forceAccess);
    }
}
