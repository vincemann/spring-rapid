package com.github.vincemann.springrapid.core.service.filter;

/**
 * all fetched and then filtered in memory
 */

public interface EntityFilter<E> extends UrlExtension<E> {
    boolean match(E entity);
}
