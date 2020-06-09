package com.github.vincemann.springrapid.coretest.service.resolve;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;

/**
 * Resolves a {@link EntityPlaceholder} to a real entity.
 */
public interface EntityPlaceholderResolver {

    public <E extends IdentifiableEntity> E resolve(EntityPlaceholder entityPlaceholder, ServiceTestContext testContext);
}
