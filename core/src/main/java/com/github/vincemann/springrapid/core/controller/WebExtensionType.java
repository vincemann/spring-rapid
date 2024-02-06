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
        if (extension instanceof QueryFilter)
            return WebExtensionType.QUERY_FILTER;
        if (extension instanceof EntityFilter)
            return WebExtensionType.ENTITY_FILTER;
        if (extension instanceof SortingExtension)
            return WebExtensionType.SORTING;
        throw new IllegalArgumentException("unknown extension type: " + extension.getClass());
    }
}
