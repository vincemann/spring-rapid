package io.github.vincemann.generic.crud.lib.test.testBundles.service.delete.failed;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.abs.failed.FailedServiceTestEntityBundle;
import lombok.Builder;

import java.io.Serializable;

public class FailedDeleteServiceTestBundle<ServiceE extends IdentifiableEntity<Id>,Id extends Serializable> extends FailedServiceTestEntityBundle<ServiceE, ServiceE, Id> {
    @Builder
    public FailedDeleteServiceTestBundle(TestCallback<ServiceE> preTestCallback, TestCallback<Id> postTestCallback, Class<? extends Exception> expectedException, ServiceE entity) {
        super(preTestCallback, postTestCallback, expectedException, entity);
    }

    public FailedDeleteServiceTestBundle(Class<? extends Exception> expectedException, ServiceE entity) {
        super(expectedException, entity);
    }

    public FailedDeleteServiceTestBundle(ServiceE entity) {
        super(entity);
    }

}
