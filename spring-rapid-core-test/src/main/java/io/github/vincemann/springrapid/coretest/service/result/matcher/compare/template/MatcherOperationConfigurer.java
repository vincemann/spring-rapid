package io.github.vincemann.springrapid.coretest.service.result.matcher.compare.template;

import io.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;

public interface MatcherOperationConfigurer {
    public ServiceResultMatcher isEqual();
    public ServiceResultMatcher isNotEqual();
}
