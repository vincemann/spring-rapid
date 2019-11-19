package io.github.vincemann.generic.crud.lib.test.testBundles.controller.create;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.IntegrationTestEntityBundle;
import lombok.Builder;

public class SuccessfulCreateIntegrationTestBundle<Dto extends IdentifiableEntity> extends IntegrationTestEntityBundle<Dto, Dto, Dto> {

    @Builder
    public SuccessfulCreateIntegrationTestBundle(TestCallback<Dto> preTestCallback, TestCallback<Dto> postTestCallback, Dto entity, TestRequestEntityModification testRequestEntityModification) {
        super(preTestCallback, postTestCallback, entity, testRequestEntityModification);
    }

    public SuccessfulCreateIntegrationTestBundle(Dto entity) {
        super(entity);
    }
}
