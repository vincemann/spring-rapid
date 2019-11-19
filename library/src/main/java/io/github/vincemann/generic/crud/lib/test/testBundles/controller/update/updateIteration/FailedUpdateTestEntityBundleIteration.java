package io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.PostIntegrationTestCallbackIdBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration.abs.UpdateTestEntityBundleIteration;
import lombok.Builder;

import java.io.Serializable;

public class FailedUpdateTestEntityBundleIteration<Dto extends IdentifiableEntity<Id>,Id extends Serializable>
        extends UpdateTestEntityBundleIteration<Dto,Dto, PostIntegrationTestCallbackIdBundle<Id>> {

    @Builder
    public FailedUpdateTestEntityBundleIteration(TestCallback<Dto> preTestCallback, TestCallback<PostIntegrationTestCallbackIdBundle<Id>> postTestCallback, Dto entity, TestRequestEntityModification testRequestEntityModification) {
        super(preTestCallback, postTestCallback, entity, testRequestEntityModification);
    }

    public FailedUpdateTestEntityBundleIteration(Dto entity) {
        super(entity);
    }
}
