package com.github.vincemann.springrapid.coretest.service.result.matcher;

import com.github.vincemann.springrapid.coretest.service.ServiceTestTemplate;

/**
 * Matcher that matches a specific condition that shall be met, after a service test is executed with {@link ServiceTestTemplate}.
 */
public interface ServiceResultMatcher {
    public void match();
}
