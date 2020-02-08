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
public class SuccessfulUpdateServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends UpdateServiceTestConfiguration<E,Id> {
    private EqualChecker<E> returnedEntityEqualChecker;


    @Builder
    public SuccessfulUpdateServiceTestConfiguration(Boolean fullUpdate, PostUpdateServiceTestCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker, EqualChecker<E> returnedEntityEqualChecker) {
        super(fullUpdate,postUpdateCallback,repoEntityEqualChecker);
        this.returnedEntityEqualChecker=returnedEntityEqualChecker;
    }

}
