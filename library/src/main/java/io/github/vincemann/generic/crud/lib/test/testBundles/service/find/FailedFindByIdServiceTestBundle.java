package io.github.vincemann.generic.crud.lib.test.testBundles.service.find;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.service.abs.failed.FailedServiceTestIdBundle;
import lombok.Builder;

import java.io.Serializable;
import java.util.Optional;

public class FailedFindByIdServiceTestBundle<Id extends Serializable,ServiceE extends IdentifiableEntity<Id>> extends FailedServiceTestIdBundle<Id, Id, Optional<ServiceE>> {

    @Builder
    public FailedFindByIdServiceTestBundle(TestCallback<Id> preTestCallback, TestCallback<Optional<ServiceE>> postTestCallback, Class<? extends Exception> expectedException, Id id) {
        super(preTestCallback, postTestCallback, expectedException, id);
    }

    public FailedFindByIdServiceTestBundle(Id id) {
        super(id);
    }

    public FailedFindByIdServiceTestBundle(Class<? extends Exception> expectedException, Id id) {
        super(expectedException, id);
    }

}
