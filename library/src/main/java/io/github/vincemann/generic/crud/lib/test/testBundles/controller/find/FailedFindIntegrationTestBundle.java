package io.github.vincemann.generic.crud.lib.test.testBundles.controller.find;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.IntegrationTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.PostIntegrationTestCallbackIdBundle;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class FailedFindIntegrationTestBundle<ServiceE extends IdentifiableEntity<Id>,Id extends Serializable>
        extends IntegrationTestEntityBundle<ServiceE,ServiceE, PostIntegrationTestCallbackIdBundle<Id>> {

    @Builder
    public FailedFindIntegrationTestBundle(TestCallback<ServiceE> preTestCallback, TestCallback<PostIntegrationTestCallbackIdBundle<Id>> postTestCallback, ServiceE entity, TestRequestEntityModification testRequestEntityModification) {
        super(preTestCallback, postTestCallback, entity, testRequestEntityModification);
    }

    public FailedFindIntegrationTestBundle(ServiceE entity) {
        super(entity);
    }
}
