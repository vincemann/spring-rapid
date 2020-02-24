package io.github.vincemann.generic.crud.lib.test.service.result.action;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.resultHandler.ServiceResultHandler;
import io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher.EntityCollectionServiceResultMatcher;
import io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher.EntityServiceResultMatcher;

public interface ServiceResultActions {
    EntityServiceResultActions andExpect(EntityServiceResultMatcher matcher);

    EntityCollectionServiceResultActions andExpect(EntityCollectionServiceResultMatcher matcher);

    ServiceResultActions andDo(ServiceResultHandler handler);

    <T> ServiceResult<T> andReturn();
}
