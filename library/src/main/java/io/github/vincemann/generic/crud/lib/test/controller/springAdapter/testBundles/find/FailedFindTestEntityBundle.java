package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.find;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.TestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostFindCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

@Getter
public class FailedFindTestEntityBundle<ServiceE extends IdentifiableEntity,Dto extends IdentifiableEntity> extends TestEntityBundle<ServiceE> {

    @Setter
    private PostFindCallback<ResponseEntity<String>> postFindCallback = (e) -> {};
    private ServiceE entityToBeFound;

    @Builder
    public FailedFindTestEntityBundle(ServiceE entity, @Nullable TestRequestEntityModification testRequestEntityModification, @Nullable PostFindCallback<ResponseEntity<String>> postFindCallback, ServiceE entityToBeFound) {
        super(entity,testRequestEntityModification);
        this.entityToBeFound = entityToBeFound;
        if(postFindCallback!=null) {
            this.postFindCallback = postFindCallback;
        }
    }

    public FailedFindTestEntityBundle(ServiceE entity, ServiceE entityToBeFound) {
        super(entity);
        this.entityToBeFound = entityToBeFound;
    }
}
