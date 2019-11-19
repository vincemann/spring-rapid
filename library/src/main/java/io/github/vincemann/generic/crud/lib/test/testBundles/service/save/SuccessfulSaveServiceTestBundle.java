package io.github.vincemann.generic.crud.lib.test.testBundles.service.save;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.CallbackableTestEntityBundle;
import lombok.Builder;

public class SuccessfulSaveServiceTestBundle<ServiceE extends IdentifiableEntity> extends CallbackableTestEntityBundle<ServiceE, ServiceE, ServiceE> {

    @Builder
    public SuccessfulSaveServiceTestBundle(TestCallback<ServiceE> preTestCallback, TestCallback<ServiceE> postTestCallback, ServiceE entity) {
        super(preTestCallback, postTestCallback, entity);
    }

    public SuccessfulSaveServiceTestBundle(ServiceE entity) {
        super(entity);
    }
}
