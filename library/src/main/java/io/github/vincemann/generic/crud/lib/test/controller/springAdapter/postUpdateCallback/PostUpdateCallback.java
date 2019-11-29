package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.postUpdateCallback;

public interface PostUpdateCallback<E> {
    public void callback(E entityToUpdate, E updatedEntity);
}
