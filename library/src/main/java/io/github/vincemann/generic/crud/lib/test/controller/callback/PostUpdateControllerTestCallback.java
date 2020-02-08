package io.github.vincemann.generic.crud.lib.test.controller.callback;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;

public interface PostUpdateControllerTestCallback<E extends IdentifiableEntity<Id>,Id extends Serializable> {
    public void callback(E afterUpdate);
}
