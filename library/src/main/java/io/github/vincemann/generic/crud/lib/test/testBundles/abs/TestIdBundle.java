package io.github.vincemann.generic.crud.lib.test.testBundles.abs;

import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallbackable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public abstract class TestIdBundle<Id extends Serializable,PreTestCallbackE,PostTestCallbackE> extends TestCallbackable<PreTestCallbackE,PostTestCallbackE> {
    private Id id;

    public TestIdBundle(Id id) {
        this.id = id;
    }

    public TestIdBundle(TestCallback<PreTestCallbackE> preTestCallback, TestCallback<PostTestCallbackE> postTestCallback, Id id) {
        super(preTestCallback, postTestCallback);
        this.id = id;
    }
}
