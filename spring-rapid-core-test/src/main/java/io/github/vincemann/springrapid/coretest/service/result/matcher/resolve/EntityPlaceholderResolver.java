package io.github.vincemann.springrapid.coretest.service.result.matcher.resolve;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;

/**
 * Resolves a {@link EntityPlaceholder} to a real entity.
 */
public interface EntityPlaceholderResolver {

    public IdentifiableEntity resolve(EntityPlaceholder entityPlaceholder, ServiceTestContext testContext);
}
