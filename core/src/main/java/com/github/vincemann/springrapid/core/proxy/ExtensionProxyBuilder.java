package com.github.vincemann.springrapid.core.proxy;


import org.apache.commons.lang3.ClassUtils;
import org.springframework.test.util.AopTestUtils;

import java.lang.reflect.Proxy;


public class ExtensionProxyBuilder<T> {
    private ExtensionProxy proxy;
    private Class<?> proxiedClass;

    public ExtensionProxyBuilder(T proxied) {
        this.proxy = new ExtensionProxy(proxied);
    }

    public ExtensionProxyBuilder(Class<T> proxiedClass){
        this.proxy = new ExtensionProxy(null);
        this.proxiedClass = proxiedClass;
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


    public ExtensionProxyBuilder<T> defaultExtensionsEnabled(boolean enabled){
        proxy.setDefaultExtensionsEnabled(enabled);
        return this;
    }


    public T build(){
        if (proxy != null){
            T unproxied = AopTestUtils.getUltimateTargetObject(proxy.getProxied());
            T proxyInstance = (T) Proxy.newProxyInstance(
                    unproxied.getClass().getClassLoader(),
                    ClassUtils.getAllInterfaces(unproxied.getClass()).toArray(new Class[0]),
                    proxy);
            return proxyInstance;
        }else{
            T proxyInstance = (T) Proxy.newProxyInstance(
                    proxiedClass.getClassLoader(),
                    ClassUtils.getAllInterfaces(proxiedClass).toArray(new Class[0]),
                    proxy);
            return proxyInstance;
        }
    }

    protected ExtensionProxy getProxy() {
        return proxy;
    }
}
