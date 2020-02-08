package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.callback.PostUpdateControllerTestCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.crudTests.config.abs.ControllerTestConfiguration;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class ControllerTestConfigurations {

    private ControllerTestConfigurations(){}

    public static <Id extends Serializable> ControllerTestConfiguration<Id> expect(HttpStatus httpStatus){
        return ControllerTestConfiguration.<Id>builder()
                .expectedHttpStatus(httpStatus)
                .build();
    }

    public static <E extends IdentifiableEntity<Id>,Id extends Serializable> UpdateControllerTestConfiguration<E,Id> partialUpdate(){
        return UpdateControllerTestConfiguration.<E,Id>Builder().fullUpdate(false).build();
    }

    public static <E extends IdentifiableEntity<Id>,Id extends Serializable> UpdateControllerTestConfiguration<E,Id> postUpdateCallback(PostUpdateControllerTestCallback<E,Id> callback){
         return UpdateControllerTestConfiguration.<E,Id>Builder()
                 .postUpdateCallback(callback)
                 .build();

    }


}
