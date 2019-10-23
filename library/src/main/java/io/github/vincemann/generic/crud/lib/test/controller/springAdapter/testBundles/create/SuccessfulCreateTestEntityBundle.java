package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.create;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostCreateCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
public class SuccessfulCreateTestEntityBundle<Dto extends IdentifiableEntity> extends TestEntityBundle<Dto> {

    @Setter
    private PostCreateCallback<Dto> postCreateCallback = (e) -> {};

    @Builder
    public SuccessfulCreateTestEntityBundle(Dto entity, @Nullable TestRequestEntityModification testRequestEntityModification, @Nullable PostCreateCallback<Dto> postCreateCallback) {
        super(entity, testRequestEntityModification);
        this.postCreateCallback=postCreateCallback;
        if(this.postCreateCallback == null){
            this.postCreateCallback= (e1) -> {};
        }
    }

    public SuccessfulCreateTestEntityBundle(Dto entity) {
        super(entity);
    }
}
