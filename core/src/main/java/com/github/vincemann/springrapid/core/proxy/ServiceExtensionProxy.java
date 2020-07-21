package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Getter
@Setter
@Slf4j
public class ServiceExtensionProxy<S extends SimpleCrudService<?,?>>
        extends AbstractExtensionServiceProxy<S, ServiceExtension<?>, AbstractExtensionServiceProxy.State, ServiceExtensionProxy>
{


    protected ServiceExtensionProxy(S proxied, ServiceExtension<?>... extensions) {
        super(proxied, extensions);
    }

    @Override
    protected State createState(Object o, Method method, Object[] args) {
        return new State(method);
    }
}
