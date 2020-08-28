package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.proxy.AbstractExtensionServiceProxy;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
/**
 * Proxy for {@link CrudService}, that applies {@link ServiceSecurityRule}s before calling service method.
 * After all Rules have been applied in the order they were given in for construction of this proxy, the {@link DefaultSecurityServiceExtension}
 * is applied if not prohibited (@see {@link OverrideDefaultSecurityRule}).
 *
 * Is created by {@link SecurityServiceExtensionProxyBuilder} or by {@link ConfigureProxies}.
 */
public class SecurityExtensionServiceProxy<S extends SimpleCrudService<?,?>>
        extends AbstractExtensionServiceProxy
        <
                S,
                SecurityExtensionServiceProxy.State,
                SecurityProxyController
                >
            implements SecurityProxyController
{

    private AbstractServiceExtension<?,? super SecurityProxyController> defaultExtension;

    public SecurityExtensionServiceProxy(S proxied, ServiceExtension<?> defaultExtension, ServiceExtension<?>... extensions) {
        super(proxied, extensions);
        this.defaultExtension = defaultExtension;
        addExtension(defaultExtension);
    }

    public SecurityExtensionServiceProxy(S proxied, ServiceExtension<?>... extensions) {
        super(proxied, extensions);
    }


    @Override
    public Object getNext(AbstractServiceExtension extension) {
        Object next = super.getNext(extension);
        if (next.equals(defaultExtension) && getState().isOverrideDefaultExtension()){
            return getProxied();
        }
        return next;
    }

    @Override
    public void overrideDefaultExtension() {
        getState().setOverrideDefaultExtension(true);
    }

    @Getter
    @Setter
    public static class State extends AbstractExtensionServiceProxy.State{
        boolean overrideDefaultExtension = false;

        public State(Method method) {
            super(method);
        }
    }

    @Override
    protected State createState(Object o, Method method, Object[] args) {
        return new State(method);
    }

    protected void setDefaultExtension(AbstractServiceExtension<?,? super SecurityProxyController> defaultExtension) {
        this.defaultExtension = defaultExtension;
    }
}
