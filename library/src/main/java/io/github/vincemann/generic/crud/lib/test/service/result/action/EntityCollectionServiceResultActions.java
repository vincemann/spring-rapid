package io.github.vincemann.generic.crud.lib.test.service.result.action;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.result.EntityCollectionServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher.EntityCollectionServiceResultMatcher;

public interface EntityCollectionServiceResultActions {
    EntityCollectionServiceResultActions andExpect(EntityCollectionServiceResultMatcher matcher) throws Exception;
    EntityCollectionServiceResult andReturn();
}
