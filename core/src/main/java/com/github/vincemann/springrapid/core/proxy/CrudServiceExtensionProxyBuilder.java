package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;

import java.io.Serializable;

/**
 * Adds additional type safe addExtension methods for CrudServices.
 * See {@link ExtensionProxyBuilder}.
 */
public class CrudServiceExtensionProxyBuilder
        <
                S extends CrudService<E, Id>,
                E extends IdentifiableEntity<Id>,
                Id extends Serializable>
        extends AbstractExtensionProxyBuilder<S, CrudServiceExtensionProxyBuilder<S, E, Id>> {

    public CrudServiceExtensionProxyBuilder(S proxied) {
        super(proxied);
    }

    /**
     * User this method if your {@link ServiceExtension} implements {@link GenericCrudServiceExtension}.
     */
    public CrudServiceExtensionProxyBuilder<S, E, ? extends Serializable> addGenericExtensions(ServiceExtension<? extends CrudService<? super E, ? extends Serializable>>... extensions) {
        for (ServiceExtension<? extends CrudService<? super E, ? extends Serializable>> extension : extensions) {
            addGenericExtension(extension);
        }
        return this;
    }

    public CrudServiceExtensionProxyBuilder<S, E, ? extends Serializable> addGenericExtension(ServiceExtension<? extends CrudService<? super E, ? extends Serializable>> extension) {
        getProxy().addExtension(extension);
        return this;
    }
}