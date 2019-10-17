package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.successfulTestBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.UpdateTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostCreateCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostDeleteCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostFindCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.successfulTestBundles.SucceedingTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.util.BeanUtils;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UpdatableSucceedingTestEntityBundle<E extends IdentifiableEntity> extends SucceedingTestEntityBundle<E> {


    //for update test
    private List<UpdateTestEntityBundle<E>> updateTestEntityBundles = new ArrayList<>();

    @Builder
    public UpdatableSucceedingTestEntityBundle(E entity, PostCreateCallback<E> postCreateCallback, PostDeleteCallback<E> postDeleteCallback, PostFindCallback<E> postFindCallback, TestRequestEntityModification findTestRequestEntityModification, TestRequestEntityModification deleteTestRequestEntityModification, TestRequestEntityModification createTestRequestEntityModification, List<UpdateTestEntityBundle<E>> updateTestEntityBundles) {
        super(entity, postCreateCallback, postDeleteCallback, postFindCallback, findTestRequestEntityModification, deleteTestRequestEntityModification, createTestRequestEntityModification);
        this.updateTestEntityBundles = updateTestEntityBundles;
    }

    public UpdatableSucceedingTestEntityBundle(E entity, List<UpdateTestEntityBundle<E>> updateTestEntityBundle) {
        super(entity);
        this.updateTestEntityBundles = updateTestEntityBundle;
        verifyBundle();
    }

    public UpdatableSucceedingTestEntityBundle(E entity, E... modifiedEntities){
        super(entity);
        for (E modEntity : modifiedEntities) {
            this.updateTestEntityBundles.add(new UpdateTestEntityBundle<>(modEntity));
        }
        verifyBundle();
    }


    @Override
    protected void verifyBundle() {
        super.verifyBundle();
        updateTestEntityBundles.forEach(bundle -> {
            Assertions.assertNotNull(bundle);
            Assertions.assertNotNull(bundle.getModifiedEntity());
            Assertions.assertFalse(BeanUtils.isDeepEqual(getEntity(),bundle.getModifiedEntity()),"ModifiedEntity must differ from Entity");
        });
    }
}
