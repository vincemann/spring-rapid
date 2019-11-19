package io.github.vincemann.generic.crud.lib.test.testBundles.service.update.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class UpdateServiceTestEntityBundle<ServiceE extends IdentifiableEntity> extends TestEntityBundle<ServiceE> {
    private TestCallback<ServiceE> preTestCallback;

    public UpdateServiceTestEntityBundle(ServiceE entity, TestCallback<ServiceE> preTestCallback) {
        super(entity);
        this.preTestCallback = preTestCallback;
    }

    public UpdateServiceTestEntityBundle(ServiceE entity) {
        super(entity);
    }

    public void callPreTestCallback(ServiceE preTestCallbackEntity){
        if(preTestCallback!=null){
            preTestCallback.callback(preTestCallbackEntity);
        }else {
            log.warn("Callback was null but still called for : '"+this+"' -> not calling it");
        }
    }
}
