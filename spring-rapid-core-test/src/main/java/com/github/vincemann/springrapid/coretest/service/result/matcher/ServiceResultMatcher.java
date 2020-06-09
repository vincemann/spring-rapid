package com.github.vincemann.springrapid.coretest.service.result.matcher;

/**
 * Matcher that matches a specific condition that shall be met, after a service test is executed with {@link com.github.vincemann.springrapid.coretest.service.ServiceTestTemplate}.
 */
public interface ServiceResultMatcher {
    public void match();
}
