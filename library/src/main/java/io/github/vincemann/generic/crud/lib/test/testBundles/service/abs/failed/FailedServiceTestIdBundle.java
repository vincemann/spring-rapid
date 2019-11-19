package io.github.vincemann.generic.crud.lib.test.testBundles.service.abs.failed;

import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class FailedServiceTestIdBundle<Id extends Serializable, PreTestCallbackE,PostTestCallbackE> extends FailedServiceTestBundle<PreTestCallbackE, PostTestCallbackE> {
    private Id id;

    public FailedServiceTestIdBundle(TestCallback<PreTestCallbackE> preTestCallback, TestCallback<PostTestCallbackE> postTestCallback, Class<? extends Exception> expectedException, Id id) {
        super(preTestCallback, postTestCallback, expectedException);
        this.id = id;
    }

    public FailedServiceTestIdBundle(Class<? extends Exception> expectedException, Id id) {
        super(expectedException);
        this.id = id;
    }

    public FailedServiceTestIdBundle(Id id) {
        this.id = id;
    }

}
