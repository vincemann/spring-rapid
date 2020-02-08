package io.github.vincemann.generic.crud.lib.test.service.crudTests.config.update.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.callback.PostUpdateServiceTestCallback;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.abs.ServiceTestConfiguration;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class UpdateServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
                                            extends ServiceTestConfiguration<E,Id> {
    private Boolean fullUpdate;
    private PostUpdateServiceTestCallback<E, Id> postUpdateCallback;

    @Builder(builderMethodName = "Builder")
    public UpdateServiceTestConfiguration(Boolean fullUpdate, PostUpdateServiceTestCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker) {
        super(repoEntityEqualChecker);
        this.fullUpdate = fullUpdate;
        this.postUpdateCallback = postUpdateCallback;
    }


}
