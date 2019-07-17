package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestEntityBundle<E extends IdentifiableEntity> {

    private E entity;
    //for update test
    private List<UpdateTestBundle<E>> updateTestBundles = new ArrayList<>();
    @Setter
    private PostCreateCallback<E> postCreateCallback = (e) -> {};
    @Setter
    private PostDeleteCallback<E> postDeleteCallback = (e) -> {};
    @Setter
    private PostFindCallback<E> postFindCallback = (e) -> {};

    /**
     *
     * @param entity
     * @param updateTestBundles  for every given {@link UpdateTestBundle}, there will be a update test,
     *                           trying to update the {@link TestEntityBundle#entity} with the {@link UpdateTestBundle#getModifiedEntity()}.
     *
     */
    @Builder
    public TestEntityBundle(E entity, List<UpdateTestBundle<E>> updateTestBundles, PostCreateCallback<E> postCreateCallback, PostDeleteCallback<E> postDeleteCallback, PostFindCallback<E> postFindCallback) {
        this.entity = entity;
        this.updateTestBundles = updateTestBundles;
        if(this.updateTestBundles==null){
            this.updateTestBundles = new ArrayList<>();
        }
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

    public TestEntityBundle(E entity) {
        this.entity = entity;
        verifyBundle();
    }

    public TestEntityBundle(E entity, UpdateTestBundle<E> updateTestBundle) {
        this.entity = entity;
        this.updateTestBundles.add(updateTestBundle);
        verifyBundle();
    }

    public TestEntityBundle(E entity, E... modifiedE){
        this.entity = entity;
        for (E modE : modifiedE) {
            this.updateTestBundles.add(new UpdateTestBundle<>(modE));
        }
        verifyBundle();
    }

    protected void verifyBundle(){
        Assertions.assertNotNull(entity);
        updateTestBundles.forEach(bundle -> {
            Assertions.assertNotNull(bundle);
            Assertions.assertNotNull(bundle.getModifiedEntity());
            Assertions.assertFalse(BeanUtils.isDeepEqual(entity,bundle.getModifiedEntity()),"E must differ from modifiedDto");
        });
    }
}
