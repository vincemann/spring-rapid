package io.github.vincemann.generic.crud.lib.test.testBundles.service.save;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.abs.failed.FailedServiceTestEntityBundle;
import lombok.Builder;

import java.io.Serializable;

public class FailedSaveServiceTestEntityBundle<ServiceE extends IdentifiableEntity<Id>,Id extends Serializable> extends FailedServiceTestEntityBundle<ServiceE, ServiceE, Id> {

    @Builder
    public FailedSaveServiceTestEntityBundle(TestCallback<ServiceE> preTestCallback, TestCallback<Id> postTestCallback, Class<? extends Exception> expectedException, ServiceE entity) {
        super(preTestCallback, postTestCallback, expectedException, entity);
    }

    public FailedSaveServiceTestEntityBundle(Class<? extends Exception> expectedException, ServiceE entity) {
        super(expectedException, entity);
    }

    public FailedSaveServiceTestEntityBundle(ServiceE entity) {
        super(entity);
    }

    public FailedSaveServiceTestEntityBundle() {
    }
}
