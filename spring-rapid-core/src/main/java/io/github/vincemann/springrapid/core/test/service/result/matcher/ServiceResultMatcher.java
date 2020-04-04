package io.github.vincemann.springrapid.core.test.service.result.matcher;

import io.github.vincemann.springrapid.core.test.service.result.ServiceTestContext;

public interface ServiceResultMatcher {
    public void match(ServiceTestContext context);
}
