package io.github.vincemann.generic.crud.lib.test.testBundles.service.abs.failed;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import lombok.Getter;

@Getter
public class FailedServiceTestEntityBundle<ServiceE extends IdentifiableEntity, PreTestCallbackE,PostTestCallbackE> extends FailedServiceTestBundle<PreTestCallbackE, PostTestCallbackE> {
    private ServiceE entity;

    public FailedServiceTestEntityBundle(TestCallback<PreTestCallbackE> preTestCallback, TestCallback<PostTestCallbackE> postTestCallback, Class<? extends Exception> expectedException, ServiceE entity) {
        super(preTestCallback, postTestCallback, expectedException);
        this.entity = entity;
    }

    public FailedServiceTestEntityBundle(Class<? extends Exception> expectedException, ServiceE entity) {
        super(expectedException);
        this.entity = entity;
    }

    public FailedServiceTestEntityBundle(ServiceE entity) {
        this.entity = entity;
    }

    public FailedServiceTestEntityBundle() {
    }
}
