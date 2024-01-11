package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

/**
 * all fetched and then filtered in memory
 */
@FunctionalInterface
public interface EntityFilter<E extends IdentifiableEntity<?>> {
    boolean match(E entity);
}
