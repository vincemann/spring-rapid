package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.create;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostCreateCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

@Getter
public class FailedCreateTestEntityBundle<E extends IdentifiableEntity> extends TestEntityBundle<E> {


    @Setter
    private PostCreateCallback<ResponseEntity<String>> postCreateCallback = (responseEntity) -> {};

    @Builder
    public FailedCreateTestEntityBundle(E entity, @Nullable PostCreateCallback<ResponseEntity<String>> postCreateCallback, @Nullable TestRequestEntityModification testRequestEntityModification) {
        super(entity,testRequestEntityModification);
        this.postCreateCallback = postCreateCallback;
    }

    public FailedCreateTestEntityBundle(E entity) {
        super(entity);
    }
}
