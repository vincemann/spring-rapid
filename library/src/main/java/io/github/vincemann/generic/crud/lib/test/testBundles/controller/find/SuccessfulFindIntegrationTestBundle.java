package io.github.vincemann.generic.crud.lib.test.testBundles.controller.find;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.IntegrationTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SuccessfulFindIntegrationTestBundle<Dto extends IdentifiableEntity,ServiceE extends IdentifiableEntity>
        extends IntegrationTestEntityBundle<ServiceE,ServiceE,Dto> {

    @Builder
    public SuccessfulFindIntegrationTestBundle(TestCallback<ServiceE> preTestCallback, TestCallback<Dto> postTestCallback, ServiceE entity, TestRequestEntityModification testRequestEntityModification) {
        super(preTestCallback, postTestCallback, entity, testRequestEntityModification);
    }

    public SuccessfulFindIntegrationTestBundle(ServiceE entity) {
        super(entity);
    }
}
