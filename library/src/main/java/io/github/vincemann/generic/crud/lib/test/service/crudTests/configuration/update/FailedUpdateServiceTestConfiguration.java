package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.abs.AbstractUpdateServiceTestConfiguration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FailedUpdateServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractUpdateServiceTestConfiguration<E, Id> {
    private Class<? extends Throwable> expectedException;

    @Builder
    public FailedUpdateServiceTestConfiguration(Boolean fullUpdate, PostUpdateCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker, Class<? extends Throwable> expectedException) {
        super(fullUpdate, postUpdateCallback, repoEntityEqualChecker);
        this.expectedException = expectedException;
    }
}
