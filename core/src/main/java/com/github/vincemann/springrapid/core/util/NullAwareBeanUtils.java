package com.github.vincemann.springrapid.core.util;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.expression.Resolver;

import java.beans.PropertyDescriptor;
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

    // also ignores id
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

    @Slf4j
    private static class WhitelistNullAwareBeanUtilsBean extends BeanUtilsBean {

        private Set<String> whiteListed = new HashSet<>();
        private Set<String> collectionsWhitelist = new HashSet<>();

        public WhitelistNullAwareBeanUtilsBean(Set<String> whiteListed, Set<String> collectionsWhitelist) {
            this.whiteListed = whiteListed;
            this.collectionsWhitelist = collectionsWhitelist;
        }

        /**
         * Copy member from value to dst only when not null
         * has whitelist for properties that, if they are null, are applied to dst
         */
        @Override
        public void copyProperty(Object dest, String name, Object value)
                throws IllegalAccessException, InvocationTargetException {
            if (name.equals("id"))
                return;
            if (!whiteListed.isEmpty()) {
                if (whiteListed.contains(name)) {
                    // value can be null, that's the purpose of the whitelist
                    simpleCopyProperty(dest, name, value);
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
                        simpleCopyProperty(dest, name, value);
                        return;
                    }
                }
                // ignore non whitelisted property
                return;
            }

            simpleCopyProperty(dest, name, value);
        }

        /**
         * just supports exact type matches and calls setter on target bean
         * if collection type, dont call setter, but call add/remove on target beans collection
         */
        public void simpleCopyProperty(final Object bean, String name, Object value)
                throws IllegalAccessException, InvocationTargetException {

            System.err.println("  copyProperty(" + bean + ", " + name + ", " + value + ")");
            // Trace logging (if enabled)
            if (log.isTraceEnabled()) {
                log.trace("  copyProperty(" + bean + ", " + name + ", " + value + ")");
            }

            // Resolve any nested expression to get the actual target bean
            Object target = bean;
            final Resolver resolver = getPropertyUtils().getResolver();
            while (resolver.hasNested(name)) {
                try {
                    target = getPropertyUtils().getProperty(target, resolver.next(name));
                    name = resolver.remove(name);
                } catch (final NoSuchMethodException e) {
                    return; // Skip this property setter
                }
            }
            if (log.isTraceEnabled()) {
                log.trace("    Target bean = " + target);
                log.trace("    Target name = " + name);
            }

            // Declare local variables we will require
            final String propName = resolver.getProperty(name); // Simple name of target property
            Class<?> type = null;                         // Java type of target property
            final int index  = resolver.getIndex(name);         // Indexed subscript value (if any)
            final String key = resolver.getKey(name);           // Mapped key value (if any)

            // Calculate the target property type
            PropertyDescriptor descriptor = null;
            try {
                descriptor = getPropertyUtils().getPropertyDescriptor(target, name);
                if (descriptor == null) {
                    return; // Skip this property setter
                }
            } catch (final NoSuchMethodException e) {
                return; // Skip this property setter
            }
            type = descriptor.getPropertyType();
            if (type == null) {
                // Most likely an indexed setter on a POJB only
                if (log.isTraceEnabled()) {
                    log.trace("    target type for property '" +
                            propName + "' is null, so skipping this setter");
                }
                return;
            }
            if (log.isTraceEnabled()) {
                log.trace("    target propName=" + propName + ", type=" +
                        type + ", index=" + index + ", key=" + key);
            }

            if (Collection.class.isAssignableFrom(type) && value instanceof Collection) {
                // Handle collections by iterating over elements and adding/removing them
                Collection<Object> targetCollection = null;
                try {
                    targetCollection = (Collection<Object>) getPropertyUtils().getSimpleProperty(target, propName);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                if (index >= 0) {
                    // Handle indexed collection properties
                    int i = 0;
                    for (Object item : (Collection<Object>) value) {
                        if (i == index) {
                            if (key != null) {
                                throw new UnsupportedOperationException("Cannot set a mapped indexed property.");
                            }
                            if (targetCollection != null) {
                                boolean containsIdentifiableEntity = false;
                                for (Object entity : targetCollection) {
                                    if (entity instanceof IdentifiableEntity) {
                                        if (ProxyUtils.jpaEquals((IdentifiableEntity) entity, (IdentifiableEntity) item)) {
                                            containsIdentifiableEntity = true;
                                            break;
                                        }
                                    }
                                }
                                if (!containsIdentifiableEntity) {
                                    targetCollection.add(item);
                                }
                            }
                        }
                        i++;
                    }
                } else if (key != null) {
                    // Handle mapped collection properties
                    throw new UnsupportedOperationException("Cannot set a mapped collection property.");
                } else {
                    // Handle regular collection properties
                    if (targetCollection != null) {
                        // Remove entities not in the source collection
                        targetCollection.removeIf(entity -> {
                            if (entity instanceof IdentifiableEntity) {
                                return !((Collection<Object>) value).stream()
                                        .anyMatch(item -> ProxyUtils.jpaEquals((IdentifiableEntity) entity, (IdentifiableEntity) item));
                            } else {
                                return !((Collection<Object>) value).contains(entity);
                            }
                        });
                        // Add entities from the source collection if not already present
                        for (Object item : (Collection<Object>) value) {
                            if (item instanceof IdentifiableEntity) {
                                boolean containsIdentifiableEntity = false;
                                for (Object entity : targetCollection) {
                                    if (entity instanceof IdentifiableEntity) {
                                        if (ProxyUtils.jpaEquals((IdentifiableEntity) entity, (IdentifiableEntity) item)) {
                                            containsIdentifiableEntity = true;
                                            break;
                                        }
                                    }
                                }
                                if (!containsIdentifiableEntity) {
                                    targetCollection.add(item);
                                }
                            } else {
                                if (!targetCollection.contains(item)) {
                                    targetCollection.add(item);
                                }
                            }
                        }
                    }
                }
            } else {
                // Set the value of the bean via calling the setter
                try {
                    getPropertyUtils().setSimpleProperty(target, propName, value);
                } catch (final NoSuchMethodException e) {
                    throw new InvocationTargetException(e, "Cannot set " + propName);
                }
            }
        }
    }




}
