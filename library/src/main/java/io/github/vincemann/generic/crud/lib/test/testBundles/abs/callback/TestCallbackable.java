package io.github.vincemann.generic.crud.lib.test.testBundles.abs.callback;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Slf4j
public abstract class TestCallbackable<PreTestCallbackE,PostTestCallbackE> {

    @Nullable
    private TestCallback<PreTestCallbackE> preTestCallback;
    @Nullable
    private TestCallback<PostTestCallbackE> postTestCallback;

    public void callPreTestCallback(PreTestCallbackE preTestCallbackEntity){
        if(preTestCallback!=null){
            preTestCallback.callback(preTestCallbackEntity);
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
