package io.github.vincemann.generic.crud.lib.test.service.result.action;

import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.handler.ServiceResultHandler;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.compare.ServiceResultMatcher;

public interface ServiceResultActions {
    ServiceResultActions andExpect(ServiceResultMatcher matcher);

    ServiceResultActions andDo(ServiceResultHandler handler);

    ServiceResult andReturn();
}
