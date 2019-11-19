package io.github.vincemann.generic.crud.lib.test.testBundles.service.delete.successful;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.CallbackableTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.TestIdBundle;
import lombok.Builder;

import java.io.Serializable;

public class SuccessfulDeleteByIdServiceTestBundle<E extends IdentifiableEntity<Id>,Id extends Serializable>
        extends CallbackableTestEntityBundle<E,E,Id> {

    public SuccessfulDeleteByIdServiceTestBundle(E entity) {
        super(entity);
    }

    @Builder
    public SuccessfulDeleteByIdServiceTestBundle(TestCallback<E> preTestCallback, TestCallback<Id> postTestCallback, E entity) {
        super(preTestCallback, postTestCallback, entity);
    }
}
