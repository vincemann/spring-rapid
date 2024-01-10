package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

@FunctionalInterface
public interface EntityFilter<E extends IdentifiableEntity<?>> {
    boolean match(E entity);
}
