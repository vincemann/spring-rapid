package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.find;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostFindCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class SuccessfulFindTestEntityBundle<Dto extends IdentifiableEntity,ServiceE extends IdentifiableEntity> extends TestEntityBundle<Dto> {

    @Setter
    private PostFindCallback<Dto> postFindCallback = (e) -> {};
    private ServiceE entityToFind;

    @Builder(builderMethodName = "Builder")
    public SuccessfulFindTestEntityBundle(Dto entity, TestRequestEntityModification testRequestEntityModification, PostFindCallback<Dto> postFindCallback, ServiceE entityToFind) {
        super(entity, testRequestEntityModification);
        this.postFindCallback = postFindCallback;
        this.entityToFind = entityToFind;
        if(this.postFindCallback == null){
            this.postFindCallback = (e1) -> {};
        }
    }

    public SuccessfulFindTestEntityBundle(Dto entity, ServiceE entityToFind) {
        super(entity);
        this.entityToFind = entityToFind;
    }
}
