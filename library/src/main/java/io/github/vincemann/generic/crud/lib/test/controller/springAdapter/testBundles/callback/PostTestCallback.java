package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback;

public interface PostTestCallback<T> {
    void callback(T requestResult);
}
