package io.github.vincemann.springrapid.coretest.util;


import io.github.vincemann.springrapid.commons.NullAwareBeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

public class NullAwareBeanUtils {

    public static void copyProperties(Object dst, Object origin){
        NullAwareBeanUtilsBean nullAwareBeanUtilsBean = new NullAwareBeanUtilsBean();
        try {
            nullAwareBeanUtilsBean.copyProperties(dst,origin);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
