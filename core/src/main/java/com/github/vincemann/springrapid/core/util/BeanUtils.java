package com.github.vincemann.springrapid.core.util;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class BeanUtils {

    public static <T> T clone(T bean){
        try {
            return (T) BeanUtilsBean.getInstance().cloneBean(bean);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
