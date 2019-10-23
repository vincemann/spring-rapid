package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.delete;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostDeleteCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SuccessfulDeleteTestEntityBundle<ServiceE extends IdentifiableEntity> extends TestEntityBundle<ServiceE> {

    @Setter
    private PostDeleteCallback<ServiceE> postDeleteCallback = (dto) -> {};

    @Builder
    public SuccessfulDeleteTestEntityBundle(ServiceE entity, TestRequestEntityModification testRequestEntityModification, PostDeleteCallback<ServiceE> postDeleteCallback) {
        super(entity, testRequestEntityModification);
        this.postDeleteCallback = postDeleteCallback;
        if(this.postDeleteCallback ==null){
            this.postDeleteCallback = (dto1) -> {};
        }
    }

    public SuccessfulDeleteTestEntityBundle(ServiceE entity) {
        super(entity);
    }
}
