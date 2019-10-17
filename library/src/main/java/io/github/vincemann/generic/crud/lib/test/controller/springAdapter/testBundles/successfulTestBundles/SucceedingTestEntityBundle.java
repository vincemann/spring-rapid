package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.successfulTestBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostCreateCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostDeleteCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostFindCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
public class SucceedingTestEntityBundle<E extends IdentifiableEntity> extends TestEntityBundle<E> {



    //CALLBACKS
    @Setter
    private PostCreateCallback<E> postCreateCallback = (e) -> {};
    @Setter
    private PostDeleteCallback<E> postDeleteCallback = (e) -> {};
    @Setter
    private PostFindCallback<E> postFindCallback = (e) -> {};



    @Builder
    public SucceedingTestEntityBundle(E entity, PostCreateCallback<E> postCreateCallback, PostDeleteCallback<E> postDeleteCallback, PostFindCallback<E> postFindCallback, @Nullable TestRequestEntityModification findTestRequestEntityModification, @Nullable TestRequestEntityModification deleteTestRequestEntityModification, @Nullable TestRequestEntityModification createTestRequestEntityModification) {
        super(entity,findTestRequestEntityModification,deleteTestRequestEntityModification,createTestRequestEntityModification);
        this.postCreateCallback = postCreateCallback;
        if(this.postCreateCallback == null){
            this.postCreateCallback= (e1) -> {};
        }
        this.postDeleteCallback = postDeleteCallback;
        if(this.postDeleteCallback ==null){
            this.postDeleteCallback = (e1) -> {};
        }
        this.postFindCallback = postFindCallback;
        if(this.postFindCallback == null){
            this.postFindCallback = (e1) -> {};
        }
        verifyBundle();
    }

    public SucceedingTestEntityBundle(E entity) {
        super(entity);
        verifyBundle();
    }
}
