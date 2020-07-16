package com.github.vincemann.springrapid.core.proxy;

import java.lang.reflect.ParameterizedType;


public class AbstractServiceExtension<T,P extends ServiceExtensionProxyController> {
    private T next;
    private P proxyController;
    @SuppressWarnings("unchecked")
    private Class<T> nextClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public AbstractServiceExtension() {
    }

    public AbstractServiceExtension(Class<T> nextClass) {
        this.nextClass = nextClass;
    }

    Class<T> getNextClass() {
        return nextClass;
    }

    void setProxyController(P proxyController) {
        this.proxyController = proxyController;
    }

    void setNext(T next) {
        this.next = next;
    }

    public T getNext() {
        return next;
    }

    public P getProxyController() {
        return proxyController;
    }
}
