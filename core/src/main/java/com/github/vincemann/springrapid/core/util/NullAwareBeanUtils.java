package com.github.vincemann.springrapid.core.util;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class NullAwareBeanUtils {


    private static class NullAwareBeanUtilsBean extends BeanUtilsBean {

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

    private static class WhitelistNullAwareBeanUtilsBean extends BeanUtilsBean{

        private Set<String> whiteListed = new HashSet<>();

        public WhitelistNullAwareBeanUtilsBean(Set<String> whiteListed) {
            this.whiteListed = whiteListed;
        }

        /**
         * Copy member from @param value to @param dst only when not null
         */
        @Override
        public void copyProperty(Object dest, String name, Object value)
                throws IllegalAccessException, InvocationTargetException {
            if(value==null){
                if (!whiteListed.contains(name)){
                    return;
                }
            }
            super.copyProperty(dest, name, value);
        }
    }

    public static void copyProperties(Object toUpdate, Object update){
        BeanUtilsBean dontCopyNull = new NullAwareBeanUtilsBean();
        try {
            dontCopyNull.copyProperties(toUpdate, update);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyProperties(Object toUpdate, Object update, Set<String> whiteList){
        BeanUtilsBean dontCopyNullButWhitelisted = new WhitelistNullAwareBeanUtilsBean(whiteList);
        try {
            dontCopyNullButWhitelisted.copyProperties(toUpdate, update);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
