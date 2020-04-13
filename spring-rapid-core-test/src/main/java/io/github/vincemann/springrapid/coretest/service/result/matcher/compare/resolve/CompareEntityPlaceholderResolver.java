package io.github.vincemann.springrapid.coretest.service.result.matcher.compare.resolve;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;

/**
 * Resolves a {@link CompareEntityPlaceholder} to a real entity.
 */
public interface CompareEntityPlaceholderResolver {

    public IdentifiableEntity resolve(CompareEntityPlaceholder compareEntityPlaceholder, ServiceTestContext testContext);
}
