package io.github.vincemann.generic.crud.lib.test.service.callback;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;

public interface PostUpdateServiceTestCallback<E extends IdentifiableEntity<Id>,Id extends Serializable> {
    public void callback(E request, E afterUpdate);
}
