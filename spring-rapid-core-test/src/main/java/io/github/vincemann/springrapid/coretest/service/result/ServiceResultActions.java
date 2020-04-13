package io.github.vincemann.springrapid.coretest.service.result;

import io.github.vincemann.springrapid.coretest.service.result.matcher.ServiceResultMatcher;

/**
 * Represents Actions that shall be executed after a test execution with {@link io.github.vincemann.springrapid.coretest.service.ServiceTestTemplate}.
 * Similar to {@link org.springframework.test.web.servlet.MockMvc}s {@link org.springframework.test.web.servlet.ResultActions}.
 */
public interface ServiceResultActions {
    ServiceResultActions andExpect(ServiceResultMatcher matcher);

    ServiceResultActions andDo(ServiceResultHandler handler);

    ServiceResult andReturn();
}
