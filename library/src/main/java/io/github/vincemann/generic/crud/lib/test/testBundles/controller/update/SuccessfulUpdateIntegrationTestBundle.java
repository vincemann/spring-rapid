package io.github.vincemann.generic.crud.lib.test.testBundles.controller.update;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.IntegrationTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration.SuccessfulUpdateTestEntityBundleIteration;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SuccessfulUpdateIntegrationTestBundle<ServiceE extends IdentifiableEntity, Dto extends IdentifiableEntity>
        extends IntegrationTestEntityBundle<ServiceE,Dto,Dto> {

    @Setter
    private List<SuccessfulUpdateTestEntityBundleIteration<Dto>> updateTestEntityBundleIterations = new ArrayList<>();

    @Builder
    public SuccessfulUpdateIntegrationTestBundle(TestCallback<Dto> preTestCallback, TestCallback<Dto> postTestCallback, ServiceE entity, TestRequestEntityModification testRequestEntityModification, List<SuccessfulUpdateTestEntityBundleIteration<Dto>> updateTestEntityBundleIterations) {
        super(preTestCallback, postTestCallback, entity, testRequestEntityModification);
        this.updateTestEntityBundleIterations = updateTestEntityBundleIterations;
    }

    public SuccessfulUpdateIntegrationTestBundle(ServiceE entity, Dto... modifiedEntities){
        super(entity);
        for (Dto modEntity : modifiedEntities) {
            this.updateTestEntityBundleIterations.add(new SuccessfulUpdateTestEntityBundleIteration<>(modEntity));
        }
    }
}
