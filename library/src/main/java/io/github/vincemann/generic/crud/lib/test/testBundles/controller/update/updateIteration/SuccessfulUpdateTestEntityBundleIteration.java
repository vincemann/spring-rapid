package io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration.abs.UpdateTestEntityBundleIteration;
import lombok.Builder;

public class SuccessfulUpdateTestEntityBundleIteration<Dto extends IdentifiableEntity>
        extends UpdateTestEntityBundleIteration<Dto, Dto, Dto> {
    @Builder
    public SuccessfulUpdateTestEntityBundleIteration(TestCallback<Dto> preTestCallback, TestCallback<Dto> postTestCallback, Dto entity, TestRequestEntityModification testRequestEntityModification) {
        super(preTestCallback, postTestCallback, entity, testRequestEntityModification);
    }

    public SuccessfulUpdateTestEntityBundleIteration(Dto entity) {
        super(entity);
    }
}
