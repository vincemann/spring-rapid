package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.postUpdateCallback;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;

public interface PostUpdateCallback<E extends IdentifiableEntity<Id>,Id extends Serializable> {
    public void callback(E request, E afterUpdate);
}
