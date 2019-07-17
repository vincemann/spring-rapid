package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.Getter;

@Getter
public class UpdateTestBundle<E extends IdentifiableEntity> {
    private E modifiedEntity;
    private PostUpdateCallback<E> postUpdateCallback = (entity) -> {};

    public UpdateTestBundle(E modifiedEntity) {
        this.modifiedEntity = modifiedEntity;
    }

    public UpdateTestBundle(E modifiedEntity, PostUpdateCallback<E> postUpdateCallback) {
        this.modifiedEntity = modifiedEntity;
        this.postUpdateCallback = postUpdateCallback;
        if(this.postUpdateCallback==null){
            this.postUpdateCallback= (entity) -> {};
        }
    }


}
