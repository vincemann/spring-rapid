package io.github.vincemann.generic.crud.lib.test.service.result.resultMatcher;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.result.EntityCollectionServiceResult;
import io.github.vincemann.generic.crud.lib.test.service.result.action.EntityCollectionServiceResultActions;

public interface EntityCollectionServiceResultMatcher {
    public void match(EntityCollectionServiceResult result);
}
