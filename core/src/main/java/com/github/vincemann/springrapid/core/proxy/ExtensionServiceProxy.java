package com.github.vincemann.springrapid.core.proxy;

import com.github.vincemann.springrapid.core.service.CrudService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Getter
@Setter
@Slf4j
public class ExtensionServiceProxy<S extends CrudService<?,?,?>>
        extends AbstractExtensionServiceProxy<S,ServiceExtension<? super S>, AbstractExtensionServiceProxy.State> {

    public ExtensionServiceProxy(S proxied, ServiceExtension<? super S>... extensions) {
        super(proxied, extensions);
    }

    @Override
    protected State createState(Object o, Method method, Object[] args) {
        return new State(method);
    }
}
