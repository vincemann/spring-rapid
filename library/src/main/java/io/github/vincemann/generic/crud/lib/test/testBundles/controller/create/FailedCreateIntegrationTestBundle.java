package io.github.vincemann.generic.crud.lib.test.testBundles.controller.create;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.IntegrationTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.PostIntegrationTestCallbackIdBundle;
import lombok.Builder;

import java.io.Serializable;

public class FailedCreateIntegrationTestBundle<Dto extends IdentifiableEntity<Id>,Id extends Serializable>
        extends IntegrationTestEntityBundle<Dto,Dto, PostIntegrationTestCallbackIdBundle<Id>> {

    @Builder
    public FailedCreateIntegrationTestBundle(TestCallback<Dto> preTestCallback, TestCallback<PostIntegrationTestCallbackIdBundle<Id>> postTestCallback, Dto entity, TestRequestEntityModification testRequestEntityModification) {
        super(preTestCallback, postTestCallback, entity, testRequestEntityModification);
    }

    public FailedCreateIntegrationTestBundle(Dto entity) {
        super(entity);
    }
}
