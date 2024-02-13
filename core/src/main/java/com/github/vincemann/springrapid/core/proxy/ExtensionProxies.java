package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;

import java.io.Serializable;

public class ExtensionProxies {

    public static <S extends CrudService<E,Id>, E extends IdentifiableEntity<Id>, Id extends Serializable> CrudServiceExtensionProxyBuilder<S,E,Id> crudProxy(S proxied){
        return new CrudServiceExtensionProxyBuilder<>(proxied);
    }

    public static <T> ExtensionProxyBuilder<T> proxy(T proxied){
        return new ExtensionProxyBuilder<>(proxied);
    }
}
