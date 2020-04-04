package io.github.vincemann.springrapid.core.test.service.result;

import io.github.vincemann.springrapid.core.test.service.result.matcher.ServiceResultMatcher;

public interface ServiceResultActions {
    ServiceResultActions andExpect(ServiceResultMatcher matcher);

    ServiceResultActions andDo(ServiceResultHandler handler);

    ServiceResult andReturn();
}
