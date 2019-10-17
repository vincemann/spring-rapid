package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.failedTestsBundles;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostFindCallback;
import lombok.Setter;

public class FailingFindTestEntityBundle {

    @Setter
    private PostFindCallback<E> postFindCallback = (e) -> {};
}
