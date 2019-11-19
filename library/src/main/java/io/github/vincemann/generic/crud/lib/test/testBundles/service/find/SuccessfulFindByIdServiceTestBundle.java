package io.github.vincemann.generic.crud.lib.test.testBundles.service.find;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.CallbackableTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import lombok.Builder;

import java.io.Serializable;
import java.util.Optional;

public class SuccessfulFindByIdServiceTestBundle<Id extends Serializable,ServiceE extends IdentifiableEntity<Id>> extends CallbackableTestEntityBundle<ServiceE, ServiceE, Optional<ServiceE>> {
    public SuccessfulFindByIdServiceTestBundle(ServiceE entity) {
        super(entity);
    }

    @Builder
    public SuccessfulFindByIdServiceTestBundle(TestCallback<ServiceE> preTestCallback, TestCallback<Optional<ServiceE>> postTestCallback, ServiceE entity) {
        super(preTestCallback, postTestCallback, entity);
    }
}
