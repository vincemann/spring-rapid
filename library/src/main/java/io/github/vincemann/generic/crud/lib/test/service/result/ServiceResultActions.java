package io.github.vincemann.generic.crud.lib.test.service.result;

import io.github.vincemann.generic.crud.lib.test.service.result.matcher.ServiceResultMatcher;

public interface ServiceResultActions {
    ServiceResultActions andExpect(ServiceResultMatcher matcher);

    ServiceResultActions andDo(ServiceResultHandler handler);

    ServiceResult andReturn();
}
