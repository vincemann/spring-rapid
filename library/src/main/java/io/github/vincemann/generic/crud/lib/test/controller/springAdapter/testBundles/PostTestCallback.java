package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;

public interface PostTestCallback<Dto extends IdentifiableEntity> {
    void callback(Dto postTestDto);
}
