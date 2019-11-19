package io.github.vincemann.generic.crud.lib.test.testBundles.service.update.updateIteration.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallbackable;
import lombok.Getter;

@Getter
public abstract class ServiceUpdateTestEntityBundleIteration<ServiceE extends IdentifiableEntity,PreTestCallbackE,PostTestCallbackE> extends TestCallbackable<PreTestCallbackE,PostTestCallbackE> {

    private ServiceE modifiedEntity;

    public ServiceUpdateTestEntityBundleIteration(ServiceE modifiedEntity) {
        this.modifiedEntity = modifiedEntity;
    }

    public ServiceUpdateTestEntityBundleIteration(TestCallback<PreTestCallbackE> preTestCallback, TestCallback<PostTestCallbackE> postTestCallback, ServiceE modifiedEntity) {
        super(preTestCallback, postTestCallback);
        this.modifiedEntity = modifiedEntity;
    }
}
