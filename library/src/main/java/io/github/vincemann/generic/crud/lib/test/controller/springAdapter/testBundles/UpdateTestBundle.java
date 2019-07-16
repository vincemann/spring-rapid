package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import lombok.Getter;

@Getter
public class UpdateTestBundle<Dto extends IdentifiableEntity> {
    private Dto modifiedDto;
    private PostUpdateCallback<Dto> postUpdateCallback = (dto) -> {};

    public UpdateTestBundle(Dto modifiedDto) {
        this.modifiedDto = modifiedDto;
    }

    public UpdateTestBundle(Dto modifiedDto, PostUpdateCallback<Dto> postUpdateCallback) {
        this.modifiedDto = modifiedDto;
        this.postUpdateCallback = postUpdateCallback;
        if(this.postUpdateCallback==null){
            this.postUpdateCallback= (dto) -> {};
        }
    }


}
