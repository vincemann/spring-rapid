package io.github.vincemann.generic.crud.lib.test.testBundles.controller.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration.FailedUpdateTestEntityBundleIteration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class FailedUpdateIntegrationTestBundle<ServiceE extends IdentifiableEntity<Id>, Dto extends IdentifiableEntity<Id>, Id extends Serializable>
{

    @Nullable
    private TestRequestEntityModification testRequestEntityModification;
    @Nullable
    private TestCallback<ServiceE> preTestCallback;
    @Setter
    private List<FailedUpdateTestEntityBundleIteration<Dto,Id>> updateTestEntityBundleIterations = new ArrayList<>();

    private ServiceE entityToUpdate;

    @Builder
    public FailedUpdateIntegrationTestBundle(@Nullable TestRequestEntityModification testRequestEntityModification, @Nullable TestCallback<ServiceE> preTestCallback, List<FailedUpdateTestEntityBundleIteration<Dto, Id>> updateTestEntityBundleIterations, ServiceE entityToUpdate) {
        this.testRequestEntityModification = testRequestEntityModification;
        this.preTestCallback = preTestCallback;
        this.updateTestEntityBundleIterations = updateTestEntityBundleIterations;
        this.entityToUpdate = entityToUpdate;
    }

    public FailedUpdateIntegrationTestBundle(ServiceE entity, Dto... modifiedEntities){
        this.entityToUpdate=entity;
        for (Dto modEntity : modifiedEntities) {
            this.updateTestEntityBundleIterations.add(new FailedUpdateTestEntityBundleIteration<>(modEntity));
        }
    }

    public void callPreTestCallback(ServiceE preTestCallbackEntity){
        if(preTestCallback!=null){
            preTestCallback.callback(preTestCallbackEntity);
        }else {
            log.warn("Callback was null but still called for : '"+this+"' -> not calling it");
        }
    }
}