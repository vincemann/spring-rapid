package io.github.vincemann.generic.crud.lib.test.service.result.action;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.result.EntityServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.ServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher.EntityServiceResultMatcher;

public interface EntityServiceResultActions {
    EntityServiceResult andReturn();
    EntityServiceResultActions andExpect(EntityServiceResultMatcher matcher) throws Exception;
}
