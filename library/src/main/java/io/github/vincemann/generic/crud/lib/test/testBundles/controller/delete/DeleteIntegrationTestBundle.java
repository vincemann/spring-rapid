package io.github.vincemann.generic.crud.lib.test.testBundles.controller.delete;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.IntegrationTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.PostIntegrationTestCallbackIdBundle;
import lombok.Builder;

import java.io.Serializable;

public class DeleteIntegrationTestBundle<ServiceE extends IdentifiableEntity<Id>,Id extends Serializable>
        extends IntegrationTestEntityBundle<ServiceE, Id, PostIntegrationTestCallbackIdBundle<Id>> {

    @Builder
    public DeleteIntegrationTestBundle(TestCallback<Id> preTestCallback, TestCallback<PostIntegrationTestCallbackIdBundle<Id>> postTestCallback, ServiceE entity, TestRequestEntityModification testRequestEntityModification) {
        super(preTestCallback, postTestCallback, entity, testRequestEntityModification);
    }

    public DeleteIntegrationTestBundle(ServiceE entity) {
        super(entity);
    }
}
