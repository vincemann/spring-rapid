package io.github.vincemann.generic.crud.lib.test.testBundles.service.update.updateIteration;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.update.updateIteration.abs.ServiceUpdateTestEntityBundleIteration;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class FailedServiceUpdateTestEntityBundleIteration<ServiceE extends IdentifiableEntity>
        extends ServiceUpdateTestEntityBundleIteration<ServiceE,ServiceE,ServiceE>
{
    @Nullable
    private Class<Exception> expectedException;

    @Builder
    public FailedServiceUpdateTestEntityBundleIteration(TestCallback<ServiceE> preTestCallback, TestCallback<ServiceE> postTestCallback, ServiceE modifiedEntity, Class<Exception> expectedException) {
        super(preTestCallback, postTestCallback, modifiedEntity);
        this.expectedException = expectedException;
    }

    public FailedServiceUpdateTestEntityBundleIteration(ServiceE modifiedEntity, Class<Exception> expectedException) {
        super(modifiedEntity);
        this.expectedException = expectedException;
    }

    public FailedServiceUpdateTestEntityBundleIteration(ServiceE modifiedEntity) {
        super(modifiedEntity);
    }
}
