package io.github.vincemann.generic.crud.lib.test.service.crudTests.config;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.service.callback.PostUpdateServiceTestCallback;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.config.update.abs.UpdateServiceTestConfiguration;

import java.io.Serializable;

public class ServiceTestConfigurations {

    private ServiceTestConfigurations(){}

    public static <E extends IdentifiableEntity<Id>,Id extends Serializable> UpdateServiceTestConfiguration<E,Id> partialUpdate(){
        return new UpdateServiceTestConfiguration<E,Id>(false,null,null);
    }

    public static <E extends IdentifiableEntity<Id>,Id extends Serializable> UpdateServiceTestConfiguration<E,Id> postUpdateCallback(PostUpdateServiceTestCallback<E,Id> callback){
        return new UpdateServiceTestConfiguration<E,Id>(null,callback,null);
    }


}
