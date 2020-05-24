package io.github.vincemann.springrapid.coretest.service.result.matcher.newcompare;

import io.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;

public interface MatcherOperationConfigurer {
    public ServiceResultMatcher isEqual();
    public ServiceResultMatcher isNotEqual();
}
