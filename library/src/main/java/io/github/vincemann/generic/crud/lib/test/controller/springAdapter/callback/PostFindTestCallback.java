package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.callback;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;

public interface PostFindTestCallback<E extends IdentifiableEntity<Id>,Id extends Serializable> {
    public void callback(E saved, IdentifiableEntity<Id> returnedDto);
}
