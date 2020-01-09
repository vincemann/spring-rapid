package io.github.vincemann.generic.crud.lib.test.service.testApi.configuration.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FailedUpdateTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractUpdateTestConfiguration<E, Id> {
    private Class<? extends Throwable> expectedException;

    @Builder
    public FailedUpdateTestConfiguration(boolean full, PostUpdateCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker) {
        super(full, postUpdateCallback,repoEntityEqualChecker);
    }
}
