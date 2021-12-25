package com.github.vincemann.springrapid.core.util;
import java.lang.reflect.InvocationTargetException;
import com.github.vincemann.springrapid.core.util.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;

public class NullAwareBeanUtilsBean extends BeanUtilsBean {

    /**
     * Copy member from @param value to @param dst only when not null
     */
    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if(value==null)return;
        super.copyProperty(dest, name, value);
    }



}
