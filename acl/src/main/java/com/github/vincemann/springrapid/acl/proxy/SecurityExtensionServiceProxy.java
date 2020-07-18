package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.proxy.AbstractExtensionServiceProxy;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
/**
 * Proxy for {@link CrudService}, that applys {@link ServiceSecurityRule}s before calling service method.
 * After all Rules have been applied in the order they were given in for construction, the {@link DefaultSecurityServiceExtension}
 * is applied if not prohibited (@see {@link OverrideDefaultSecurityRule})
 *
 * Is created by {@link SecurityExtensionServiceProxyFactory} or by {@link ConfigureProxies}.
 */
class SecurityExtensionServiceProxy<S extends CrudService<?,?,?>>
        extends AbstractExtensionServiceProxy<S,SecurityServiceExtension<? super S>, SecurityExtensionServiceProxy.State,SecurityProxyController>
            implements SecurityProxyController
{

    private SecurityServiceExtension<? super S> defaultExtension;

    public SecurityExtensionServiceProxy(S proxied, SecurityServiceExtension<? super S> defaultExtension, SecurityServiceExtension<? super S>... extensions) {
        super(proxied,defaultExtension);
        addExtension(extensions);
        this.defaultExtension = defaultExtension;
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
        getState().overrideDefaultExtension=true;
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
}
