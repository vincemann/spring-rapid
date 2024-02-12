package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;

import java.io.Serializable;

public class CrudServiceExtensionProxyBuilder
        <S extends CrudService<E,Id>,E extends IdentifiableEntity<Id>, Id extends Serializable>
        extends ExtensionProxyBuilder<S>{

    public CrudServiceExtensionProxyBuilder(S proxied) {
        super(proxied);
    }

    /**
     * User this method if your {@link ServiceExtension} implements {@link GenericCrudServiceExtension}.
     */
    public CrudServiceExtensionProxyBuilder<S,E,Id> addGenericExtensions(ServiceExtension<? extends CrudService<? super E,? super Id>>... extensions){
        for (ServiceExtension<? extends CrudService<? super E, ? super Id>> extension : extensions) {
            addGenericExtension(extension);
        }
        return this;
    }

    public CrudServiceExtensionProxyBuilder<S,E,Id> addGenericExtension(ServiceExtension<? extends CrudService<? super E,? super Id>> extension){
        getProxy().addExtension(extension);
        return this;
    }
}