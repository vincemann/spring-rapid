package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback;

public interface PostTestCallback<Dto> {
    void callback(Dto postTestDto);
}
