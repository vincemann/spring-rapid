package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

public class SecurityServiceExtensionProxyBuilderFactory {

    private SecurityServiceExtension<?> defaultSecurityExtension;

    @Autowired
    public SecurityServiceExtensionProxyBuilderFactory(@DefaultSecurityServiceExtension SecurityServiceExtension<?> defaultSecurityExtension) {
        this.defaultSecurityExtension = defaultSecurityExtension;
    }

    public <S extends SimpleCrudService<E,Id>,E extends IdentifiableEntity<Id>, Id extends Serializable> SecurityServiceExtensionProxyBuilder<S,E,Id> create(S proxied){
        return new SecurityServiceExtensionProxyBuilder<>(proxied,defaultSecurityExtension);
    }
}
