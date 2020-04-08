package io.github.vincemann.springrapid.coretest.service.result.matcher;

import io.github.vincemann.springrapid.coretest.service.result.ServiceTestContext;

public interface ServiceResultMatcher {
    public void match(ServiceTestContext context);
}
