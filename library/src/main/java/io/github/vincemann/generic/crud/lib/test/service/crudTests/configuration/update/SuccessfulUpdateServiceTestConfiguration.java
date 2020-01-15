package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.callback.PostUpdateServiceTestCallback;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.abs.AbstractUpdateServiceTestConfiguration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SuccessfulUpdateServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends AbstractUpdateServiceTestConfiguration<E,Id> {
    private EqualChecker<E> returnedEntityEqualChecker;

    @Builder
    public SuccessfulUpdateServiceTestConfiguration(Boolean fullUpdate, PostUpdateServiceTestCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) {
        super(fullUpdate,postUpdateCallback,repoEntityEqualChecker);
        this.returnedEntityEqualChecker=returnedEntityEqualChecker;
    }

}
