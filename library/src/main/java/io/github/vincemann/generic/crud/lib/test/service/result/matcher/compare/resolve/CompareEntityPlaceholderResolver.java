package io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare.resolve;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceTestContext;

public interface CompareEntityPlaceholderResolver {

    public IdentifiableEntity resolve(CompareEntityPlaceholder compareEntityPlaceholder, ServiceTestContext testContext);
}
