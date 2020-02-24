package io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher;

import io.github.vincemann.generic.crud.lib.test.service.result.EntityServiceResult;

@FunctionalInterface
public interface EntityServiceResultMatcher {
    public void match(EntityServiceResult result);
}
