package io.github.vincemann.generic.crud.lib.test.testBundles.service.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.CallbackableTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.abs.UpdateServiceTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.updateIteration.FailedServiceUpdateTestEntityBundleIteration;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FailedUpdateServiceTestEntityBundle<ServiceE extends IdentifiableEntity>
        extends UpdateServiceTestEntityBundle<ServiceE> {
    private List<FailedServiceUpdateTestEntityBundleIteration<ServiceE>> updateTestEntityBundleIterations = new ArrayList<>();


    @Builder
    public FailedUpdateServiceTestEntityBundle(ServiceE entity, TestCallback<ServiceE> preTestCallback, List<FailedServiceUpdateTestEntityBundleIteration<ServiceE>> updateTestEntityBundleIterations) {
        super(entity, preTestCallback);
        this.updateTestEntityBundleIterations = updateTestEntityBundleIterations;
    }

    public FailedUpdateServiceTestEntityBundle(ServiceE entity, List<FailedServiceUpdateTestEntityBundleIteration<ServiceE>> updateTestEntityBundleIterations) {
        super(entity);
        this.updateTestEntityBundleIterations = updateTestEntityBundleIterations;
    }


    public FailedUpdateServiceTestEntityBundle(ServiceE entity) {
        super(entity);
    }

    public FailedUpdateServiceTestEntityBundle(ServiceE entity, ServiceE... mods) {
        super(entity);
        for (ServiceE mod : mods) {
            updateTestEntityBundleIterations.add(new FailedServiceUpdateTestEntityBundleIteration<>(mod));
        }
    }
}
