package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.find;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostFindCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SuccessfulFindTestEntityBundle<Dto extends IdentifiableEntity,ServiceE extends IdentifiableEntity> extends TestEntityBundle<ServiceE> {

    @Setter
    private PostFindCallback<Dto> postFindCallback = (e) -> {};

    @Builder(builderMethodName = "Builder")
    public SuccessfulFindTestEntityBundle(ServiceE entity, TestRequestEntityModification testRequestEntityModification, PostFindCallback<Dto> postFindCallback) {
        super(entity, testRequestEntityModification);
        this.postFindCallback = postFindCallback;
        if(this.postFindCallback == null){
            this.postFindCallback = (e1) -> {};
        }
    }

    public SuccessfulFindTestEntityBundle(ServiceE entity) {
        super(entity);
    }
}
