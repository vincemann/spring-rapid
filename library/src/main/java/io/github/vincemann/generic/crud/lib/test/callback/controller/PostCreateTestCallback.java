package io.github.vincemann.generic.crud.lib.test.callback.controller;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

import java.io.Serializable;

public interface PostCreateTestCallback<E extends IdentifiableEntity<Id>,Id extends Serializable> {
    public void callback(E saved, IdentifiableEntity<Id> returnedDto);
}
