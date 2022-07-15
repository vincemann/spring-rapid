package com.github.vincemann.springrapid.core.util;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class NullAwareBeanUtils {


    public static void copyProperties(Object toUpdate, Object update) {
        BeanUtilsBean dontCopyNull = new NullAwareBeanUtilsBean();
        try {
            dontCopyNull.copyProperties(toUpdate, update);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyProperties(Object toUpdate, Object update, Set<String> collectionsWhitelist, Set<String> whiteList) {
        BeanUtilsBean dontCopyNullButWhitelisted = new WhitelistNullAwareBeanUtilsBean(whiteList, collectionsWhitelist);
        try {
            dontCopyNullButWhitelisted.copyProperties(toUpdate, update);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static class NullAwareBeanUtilsBean extends BeanUtilsBean {

        /**
         * Copy member from @param value to @param dst only when not null
         */
        @Override
        public void copyProperty(Object dest, String name, Object value)
                throws IllegalAccessException, InvocationTargetException {
            if (value == null) return;
            super.copyProperty(dest, name, value);
        }
    }

    private static class WhitelistNullAwareBeanUtilsBean extends BeanUtilsBean {

        private Set<String> whiteListed = new HashSet<>();
        private Set<String> collectionsWhitelist = new HashSet<>();

        public WhitelistNullAwareBeanUtilsBean(Set<String> whiteListed, Set<String> collectionsWhitelist) {
            this.whiteListed = whiteListed;
            this.collectionsWhitelist = collectionsWhitelist;
        }

        /**
         * Copy member from @param value to @param dst only when not null
         */
        @Override
        public void copyProperty(Object dest, String name, Object value)
                throws IllegalAccessException, InvocationTargetException {
            if (!whiteListed.isEmpty()) {
                if (whiteListed.contains(name)) {
                    // value can be null, that's the purpose of the whitelist
                    super.copyProperty(dest, name, value);
                    return;
                }
            }

            if (value == null) {
                // ignore null value
                return;
            }
            if (Collection.class.isAssignableFrom(value.getClass())){
                if (!collectionsWhitelist.isEmpty()){
                    if (collectionsWhitelist.contains(name)){
                        super.copyProperty(dest, name, value);
                        return;
                    }
                }
                // ignore non whitelisted property
                return;
            }

            super.copyProperty(dest, name, value);
        }
    }
}
