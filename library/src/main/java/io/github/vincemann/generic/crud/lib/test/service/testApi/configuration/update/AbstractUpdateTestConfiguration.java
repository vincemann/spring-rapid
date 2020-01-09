package io.github.vincemann.generic.crud.lib.test.service.testApi.configuration.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.testApi.configuration.abs.AbstractTestConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AbstractUpdateTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
                                            extends AbstractTestConfiguration<E,Id> {
    private boolean fullUpdate;
    private PostUpdateCallback<E, Id> postUpdateCallback;

    public AbstractUpdateTestConfiguration(boolean fullUpdate, PostUpdateCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker) {
        super(repoEntityEqualChecker);
        this.fullUpdate = fullUpdate;
        this.postUpdateCallback = postUpdateCallback;
    }


}
