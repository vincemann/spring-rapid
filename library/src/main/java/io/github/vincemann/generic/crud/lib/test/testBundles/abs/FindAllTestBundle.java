package io.github.vincemann.generic.crud.lib.test.testBundles.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.findAllEntitesTestProvider.FindAllTestEntitiesProvider;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.NoArgsTestCallback;
import io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback.TestCallback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.Set;

@Getter
@Setter
@Slf4j
public abstract class FindAllTestBundle<ServiceE extends IdentifiableEntity, PostTestCallbackE>  {


    @Nullable private NoArgsTestCallback preTestCallback;
    @Nullable private TestCallback<PostTestCallbackE> postTestCallback;
              private Set<ServiceE> entitiesSavedBeforeRequest;
              private FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider;

    public FindAllTestBundle(@Nullable NoArgsTestCallback preTestCallback, @Nullable TestCallback<PostTestCallbackE> postTestCallback, Set<ServiceE> entitiesSavedBeforeRequest, FindAllTestEntitiesProvider<ServiceE> findAllTestEntitiesProvider) {
        this.preTestCallback = preTestCallback;
        this.postTestCallback = postTestCallback;
        this.entitiesSavedBeforeRequest = entitiesSavedBeforeRequest;
        this.findAllTestEntitiesProvider = findAllTestEntitiesProvider;
    }

    public void callPreTestCallback(){
        if(preTestCallback!=null){
            preTestCallback.callback();
        }else {
            log.warn("Callback was null but still called for : '"+this+"' -> not calling it");
        }
    }

    public void callPostTestCallback(PostTestCallbackE postTestCallbackEntity){
        if(postTestCallback!=null){
            postTestCallback.callback(postTestCallbackEntity);
        }else {
            log.warn("Callback was null but still called for : '"+this+"' -> not calling it");
        }
    }
}
