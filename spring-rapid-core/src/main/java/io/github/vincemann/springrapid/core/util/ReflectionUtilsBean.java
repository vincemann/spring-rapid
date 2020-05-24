package io.github.vincemann.springrapid.core.util;

import io.github.vincemann.springrapid.commons.ReflectionUtils;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

public class ReflectionUtilsBean {

    public static ReflectionUtilsBean instance;

    @Cacheable("reflections")
    public Set<String> getProperties(Class c){
        return ReflectionUtils.getProperties(c);
    }

    @Cacheable("reflections")
    public Set<Field> getFields(Class clazz) {
        return ReflectionUtils.getFields(clazz);
    }

    @Cacheable("reflections")
    public Map<String, Field> getNameFieldMap(Class<?> clazz){
        return ReflectionUtils.getNameFieldMap(clazz);
    }

    @Cacheable("reflections")
    public Field[] getFieldsWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls){
        return ReflectionUtilsBean.instance.getFieldsWithAnnotation(cls,annotationCls);
    }

//    @Cacheable("reflections")
//    public Field getField(final Class<?> cls, final String fieldName, final boolean forceAccess){
//        return FieldUtils.getField(cls,fieldName,forceAccess);
//    }
}
