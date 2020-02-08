package io.github.vincemann.generic.crud.lib.test.service.crudTests.config.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.callback.PostUpdateServiceTestCallback;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.update.abs.UpdateServiceTestConfiguration;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class FailedUpdateServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends UpdateServiceTestConfiguration<E, Id> {
    private Class<? extends Throwable> expectedException;

    @Builder
    public FailedUpdateServiceTestConfiguration(Boolean fullUpdate, PostUpdateServiceTestCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker, Class<? extends Throwable> expectedException) {
        super(fullUpdate, postUpdateCallback, repoEntityEqualChecker);
        this.expectedException = expectedException;
    }
}
