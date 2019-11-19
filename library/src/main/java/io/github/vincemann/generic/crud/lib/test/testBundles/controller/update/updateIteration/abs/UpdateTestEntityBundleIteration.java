package io.github.vincemann.generic.crud.lib.test.testBundles.controller.update.updateIteration.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallbackable;
import io.github.vincemann.generic.crud.lib.test.testBundles.controller.IntegrationTestEntityBundle;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public abstract class UpdateTestEntityBundleIteration<Dto extends IdentifiableEntity,PreTestCallbackE,PostTestCallbackE>
        extends IntegrationTestEntityBundle<Dto,PreTestCallbackE,PostTestCallbackE> {


    public UpdateTestEntityBundleIteration(TestCallback<PreTestCallbackE> preTestCallback, TestCallback<PostTestCallbackE> postTestCallback, Dto entity, TestRequestEntityModification testRequestEntityModification) {
        super(preTestCallback, postTestCallback, entity, testRequestEntityModification);
    }

    public UpdateTestEntityBundleIteration(Dto entity) {
        super(entity);
    }
}
