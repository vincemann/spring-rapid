package com.github.vincemann.springrapid.core.service.filter;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;

/**
 * all fetched and then filtered in memory
 */

public interface EntityFilter<E extends IdentifiableEntity<?>> extends ArgAware {
    boolean match(E entity);
}
