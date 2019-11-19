package io.github.vincemann.generic.crud.lib.test.testBundles.service.delete.failed;

import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.abs.failed.FailedServiceTestIdBundle;
import lombok.Builder;

import java.io.Serializable;

public class FailedDeleteByIdServiceTestBundle<Id extends Serializable> extends FailedServiceTestIdBundle<Id, Id, Id> {

    @Builder
    public FailedDeleteByIdServiceTestBundle(TestCallback<Id> preTestCallback, TestCallback<Id> postTestCallback, Class<? extends Exception> expectedException, Id id) {
        super(preTestCallback, postTestCallback, expectedException, id);
    }

    public FailedDeleteByIdServiceTestBundle(Id id) {
        super(id);
    }

    public FailedDeleteByIdServiceTestBundle(Class<? extends Exception> expectedException, Id id) {
        super(expectedException, id);
    }
}
