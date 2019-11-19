package io.github.vincemann.generic.crud.lib.test.testBundles.service.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.abs.UpdateServiceTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.updateIteration.SuccessfulServiceUpdateTestEntityBundleIteration;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SuccessfulUpdateServiceTestEntityBundle<ServiceE extends IdentifiableEntity>
        extends UpdateServiceTestEntityBundle<ServiceE> {
    private List<SuccessfulServiceUpdateTestEntityBundleIteration<ServiceE>> updateTestEntityBundleIterations = new ArrayList<>();

    @Builder
    public SuccessfulUpdateServiceTestEntityBundle(ServiceE entity, TestCallback<ServiceE> preTestCallback, List<SuccessfulServiceUpdateTestEntityBundleIteration<ServiceE>> updateTestEntityBundleIterations) {
        super(entity, preTestCallback);
        this.updateTestEntityBundleIterations = updateTestEntityBundleIterations;
    }

    public SuccessfulUpdateServiceTestEntityBundle(ServiceE entity, ServiceE... mods) {
        super(entity);
        for (ServiceE mod : mods) {
            updateTestEntityBundleIterations.add(new SuccessfulServiceUpdateTestEntityBundleIteration<>(mod));
        }
    }
}
