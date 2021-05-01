package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.service.CrudService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Getter
@Setter
@Slf4j
public class ServiceExtensionProxy<S extends CrudService<?,?>>
        extends AbstractExtensionServiceProxy<S, AbstractExtensionServiceProxy.State, ProxyController>
{

    private Boolean defaultExtensionsEnabled = Boolean.TRUE;

    protected ServiceExtensionProxy(S proxied, BasicServiceExtension<?>... extensions) {
        super(proxied, extensions);
    }

    @Override
    protected State createState(Object o, Method method, Object[] args) {
        return new State(method);
    }

    @Override
    public String toString() {
        return "ServiceExtensionProxy{ " +
                "for entity: " + getProxied().getEntityClass() +
                " }";
    }
}
