package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.failedTestsBundles;

import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostDeleteCallback;
import lombok.Setter;

public class FailingDeleteTestEntityBundle extends TestEntityBundle {

    @Setter
    private PostDeleteCallback<E> postDeleteCallback = (e) -> {};

}
