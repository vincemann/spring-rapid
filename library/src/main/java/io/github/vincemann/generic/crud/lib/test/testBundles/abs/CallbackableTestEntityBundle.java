package io.github.vincemann.generic.crud.lib.test.testBundles.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallbackable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CallbackableTestEntityBundle<E extends IdentifiableEntity,PreTestCallbackE, PostTestCallbackE>
        extends TestCallbackable<PreTestCallbackE,PostTestCallbackE> {
    private E entity;

    public CallbackableTestEntityBundle(E entity) {
        this.entity = entity;
    }

    public CallbackableTestEntityBundle(TestCallback<PreTestCallbackE> preTestCallback, TestCallback<PostTestCallbackE> postTestCallback, E entity) {
        super(preTestCallback, postTestCallback);
        this.entity = entity;
    }
}
