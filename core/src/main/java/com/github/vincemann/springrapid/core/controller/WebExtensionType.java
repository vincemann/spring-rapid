package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.service.filter.EntityFilter;
import com.github.vincemann.springrapid.core.service.filter.WebExtension;
import com.github.vincemann.springrapid.core.service.filter.jpa.QueryFilter;
import com.github.vincemann.springrapid.core.service.filter.jpa.SortingExtension;

public enum WebExtensionType {
    ENTITY_FILTER,
    QUERY_FILTER,
    SORTING;

    public static WebExtensionType get(WebExtension<?> extension){
       return get(extension.getClass());
    }

    public static WebExtensionType get(Class<?> clazz){
        if (QueryFilter.class.isAssignableFrom(clazz)){
            return WebExtensionType.QUERY_FILTER;
        }
        if (EntityFilter.class.isAssignableFrom(clazz)){
            return WebExtensionType.ENTITY_FILTER;
        }
        if (SortingExtension.class.isAssignableFrom(clazz)){
            return WebExtensionType.SORTING;
        }
        throw new IllegalArgumentException("unknown extension type: " + clazz);
    }
}
