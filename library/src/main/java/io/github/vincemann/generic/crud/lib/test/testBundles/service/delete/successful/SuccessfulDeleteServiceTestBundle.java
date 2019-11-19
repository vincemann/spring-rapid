package io.github.vincemann.generic.crud.lib.test.testBundles.service.delete.successful;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.CallbackableTestEntityBundle;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SuccessfulDeleteServiceTestBundle<ServiceE extends IdentifiableEntity<Id>,Id extends Serializable> extends CallbackableTestEntityBundle<ServiceE,ServiceE,Id> {

    @Builder
    public SuccessfulDeleteServiceTestBundle(TestCallback<ServiceE> preTestCallback, TestCallback<Id> postTestCallback, ServiceE entity) {
        super(preTestCallback, postTestCallback, entity);
    }

    public SuccessfulDeleteServiceTestBundle(ServiceE entity) {
        super(entity);
    }
}
