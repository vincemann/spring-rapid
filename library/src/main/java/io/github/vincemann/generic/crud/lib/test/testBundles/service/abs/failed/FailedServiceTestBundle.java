package io.github.vincemann.generic.crud.lib.test.testBundles.service.abs.failed;

import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallbackable;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public abstract class FailedServiceTestBundle<PreTestCallbackE,PostTestCallbackE> extends TestCallbackable<PreTestCallbackE, PostTestCallbackE> {
    @Nullable
    private Class<? extends Exception> expectedException;

    public FailedServiceTestBundle(TestCallback<PreTestCallbackE> preTestCallback, TestCallback<PostTestCallbackE> postTestCallback, Class<? extends Exception> expectedException) {
        super(preTestCallback, postTestCallback);
        this.expectedException = expectedException;
    }

    public FailedServiceTestBundle(Class<? extends Exception> expectedException) {
        this.expectedException = expectedException;
    }

    public FailedServiceTestBundle() {
    }
}
