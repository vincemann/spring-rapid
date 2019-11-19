package io.github.vincemann.generic.crud.lib.test.testBundles.service.update.updateIteration;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.updateIteration.abs.ServiceUpdateTestEntityBundleIteration;

public class SuccessfulServiceUpdateTestEntityBundleIteration<ServiceE extends IdentifiableEntity> extends ServiceUpdateTestEntityBundleIteration<ServiceE, ServiceE, ServiceE> {
    public SuccessfulServiceUpdateTestEntityBundleIteration(ServiceE modifiedEntity) {
        super(modifiedEntity);
    }
}
