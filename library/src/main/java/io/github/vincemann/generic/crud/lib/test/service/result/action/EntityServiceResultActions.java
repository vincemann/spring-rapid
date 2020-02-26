package io.github.vincemann.generic.crud.lib.test.service.result.action;

import io.github.vincemann.generic.crud.lib.test.service.result.EntityServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.matcher.EntityServiceResultMatcher;

public interface EntityServiceResultActions {
    EntityServiceResult andReturn();
    EntityServiceResultActions andExpect(EntityServiceResultMatcher matcher) throws Exception;
}
