package com.github.vincemann.springrapid.core.proxy;

import org.apache.commons.lang3.ClassUtils;
import org.springframework.test.util.AopTestUtils;

import java.lang.reflect.Proxy;

public class AbstractExtensionProxyBuilder<T, B extends AbstractExtensionProxyBuilder<T, B>> {

    private ExtensionProxy proxy;

    public AbstractExtensionProxyBuilder(T proxied) {
        this.proxy = new ExtensionProxy(proxied);
    }

    public B addExtensions(ServiceExtension<? super T>... extensions) {
        for (ServiceExtension<? super T> extension : extensions) {
            proxy.addExtension(extension);
        }
        return (B) this;
    }

    public B addExtension(ServiceExtension<? super T> extension) {
        proxy.addExtension(extension);
        return (B) this;
    }

    public B ignoreDefaultExtensions(Class<? extends ServiceExtension<? super T>>... extensions) {
        for (Class<? extends ServiceExtension<? super T>> extension : extensions) {
            proxy.ignoreExtension(extension);
        }
        return (B) this;
    }

    public B disableDefaultExtensions() {
        return defaultExtensionsEnabled(false);
    }

    public B defaultExtensionsEnabled(boolean enabled) {
        proxy.setDefaultExtensionsEnabled(enabled);
        return (B) this;
    }


    public T build() {
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
