package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;


public class ExtensionProxyBuilder<T> {
    private final ExtensionProxy proxy;

    public ExtensionProxyBuilder(T proxied) {
        this.proxy = new ExtensionProxy(proxied);
    }

    public ExtensionProxyBuilder<T> addExtensions(ServiceExtension<? super T>... extensions){
        for (ServiceExtension<? super T> extension : extensions) {
            proxy.addExtension(extension);
        }
        return this;
    }

    public ExtensionProxyBuilder<T> addExtension(ServiceExtension<? super T> extension){
        proxy.addExtension(extension);
        return this;
    }

    public ExtensionProxyBuilder<T> ignoreDefaultExtensions(Class<? extends ServiceExtension<? super T>>... extensions){
        for (Class<? extends ServiceExtension<? super T>> extension : extensions) {
            proxy.ignoreExtension(extension);
        }
        return this;
    }


    public ExtensionProxyBuilder<T> setDefaultExtensionsEnabled(boolean enabled){
        proxy.setDefaultExtensionsEnabled(enabled);
        return this;
    }


    public T build(){
        T unproxied = AopTestUtils.getUltimateTargetObject(proxy.getProxied());
        T proxyInstance = (T) Proxy.newProxyInstance(
                unproxied.getClass().getClassLoader(),
                ClassUtils.getAllInterfaces(unproxied.getClass()).toArray(new Class[0]),
                proxy);
        return proxyInstance;
    }

    protected ExtensionProxy getProxy() {
        return proxy;
    }
}
