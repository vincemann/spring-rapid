package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.delete;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostDeleteCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
public class FailedDeleteTestEntityBundle<ServiceE extends IdentifiableEntity> extends TestEntityBundle<ServiceE> {

    @Setter
    private PostDeleteCallback<ServiceE> postDeleteCallback = (serviceE) -> {};

    @Builder
    public FailedDeleteTestEntityBundle(ServiceE entity, @Nullable PostDeleteCallback<ServiceE> postDeleteCallback, @Nullable TestRequestEntityModification testRequestEntityModification) {
        super(entity,testRequestEntityModification);
        if(postDeleteCallback!=null) {
            this.postDeleteCallback = postDeleteCallback;
        }
    }

    public FailedDeleteTestEntityBundle(ServiceE entity) {
        super(entity);
    }
}
