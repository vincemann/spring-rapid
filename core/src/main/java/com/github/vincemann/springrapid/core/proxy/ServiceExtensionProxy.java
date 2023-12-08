package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.service.CrudService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Slf4j
public class ServiceExtensionProxy<S extends CrudService<?,?>>
        extends AbstractExtensionServiceProxy<S, AbstractExtensionServiceProxy.State, ProxyController>
{

    private Boolean defaultExtensionsEnabled = Boolean.TRUE;
    private Set<Class<? extends AbstractServiceExtension>> defaultExtensionsIgnored = new HashSet<>();

    protected ServiceExtensionProxy(S proxied, BasicServiceExtension<?>... extensions) {
        super(proxied, extensions);
    }

    public void ignoreExtension(Class<? extends AbstractServiceExtension> clazz){
        defaultExtensionsIgnored.add(clazz);
    }

    public boolean isIgnored(Class<? extends AbstractServiceExtension> clazz) {
        return !defaultExtensionsIgnored.contains(clazz);
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
