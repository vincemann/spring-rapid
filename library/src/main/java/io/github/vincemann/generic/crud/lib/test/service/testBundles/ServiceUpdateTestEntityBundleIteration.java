package io.github.vincemann.generic.crud.lib.test.service.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostUpdateCallback;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class ServiceUpdateTestEntityBundleIteration<ServiceE extends IdentifiableEntity> {

    private ServiceE modifiedEntity;
    @Nullable
    private PostUpdateCallback<ServiceE> postUpdateCallback = (entity) -> {};

    @Builder
    public ServiceUpdateTestEntityBundleIteration(ServiceE modifiedEntity, @Nullable PostUpdateCallback<ServiceE> postUpdateCallback) {
        this.modifiedEntity = modifiedEntity;
        this.postUpdateCallback = postUpdateCallback;
        if(this.postUpdateCallback==null){
            this.postUpdateCallback= (entity) -> {};
        }
    }

    public ServiceUpdateTestEntityBundleIteration(ServiceE modifiedEntity) {
        this.modifiedEntity = modifiedEntity;
    }
}
