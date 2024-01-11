package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

/**
 * all fetched and then filtered in memory
 */

public interface EntityFilter<E extends IdentifiableEntity<?>> extends ArgAwareFilter {
    boolean match(E entity);
}
