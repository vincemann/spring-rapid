package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.abs.AbstractUpdateTestConfiguration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SuccessfulUpdateTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractUpdateTestConfiguration<E,Id> {
    private EqualChecker<E> returnedEntityEqualChecker;

    @Builder
    public SuccessfulUpdateTestConfiguration(Boolean fullUpdate, PostUpdateCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) {
        super(fullUpdate,postUpdateCallback,repoEntityEqualChecker);
        this.returnedEntityEqualChecker=returnedEntityEqualChecker;
    }

}
