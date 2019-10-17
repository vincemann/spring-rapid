package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.failedTestsBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostCreateCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

public class FailingCreateTestEntityBundle<E extends IdentifiableEntity> extends TestEntityBundle<E> {


    //CALLBACKS
    @Setter
    private PostCreateCallback<ResponseEntity<String>> postCreateCallback = (responseEntity) -> {};

    @Builder
    public FailingCreateTestEntityBundle(E entity, TestRequestEntityModification findTestRequestEntityModification, TestRequestEntityModification deleteTestRequestEntityModification, TestRequestEntityModification createTestRequestEntityModification) {
        super(entity, findTestRequestEntityModification, deleteTestRequestEntityModification, createTestRequestEntityModification);
    }

    public FailingCreateTestEntityBundle(E entity) {
        super(entity);
    }
}
