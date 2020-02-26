package io.github.vincemann.generic.crud.lib.test.service.result.action;

import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.handler.ServiceResultHandler;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.EntityCollectionServiceResultMatcher;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.EntityServiceResultMatcher;

public interface ServiceResultActions {
    EntityServiceResultActions andExpect(EntityServiceResultMatcher matcher);

    EntityCollectionServiceResultActions andExpect(EntityCollectionServiceResultMatcher matcher);

    ServiceResultActions andDo(ServiceResultHandler handler);

    <T> ServiceResult<T> andReturn();
}
