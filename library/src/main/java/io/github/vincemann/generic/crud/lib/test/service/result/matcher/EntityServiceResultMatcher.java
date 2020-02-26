package io.github.vincemann.generic.crud.lib.test.service.result.matcher;

import io.github.vincemann.generic.crud.lib.test.service.result.EntityServiceResult;

@FunctionalInterface
public interface EntityServiceResultMatcher {
    public void match(EntityServiceResult result);
}
