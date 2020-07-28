package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.Serializable;

public class SecurityServiceExtensionProxyBuilderFactory implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public <S extends SimpleCrudService<E,Id>,E extends IdentifiableEntity<Id>, Id extends Serializable> SecurityServiceExtensionProxyBuilder<S,E,Id>
    create(S proxied){
        SecurityServiceExtension<?> defaultSecurityExtension = (SecurityServiceExtension<?>) context.getBean("defaultServiceSecurityRule");
        return new SecurityServiceExtensionProxyBuilder<>(proxied,defaultSecurityExtension);
    }


}
