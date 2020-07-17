package com.github.vincemann.springrapid.core.proxy;

import java.lang.reflect.ParameterizedType;
import java.util.List;


public class AbstractServiceExtension<T,P extends ServiceExtensionProxyController> {
    private P proxyController;
    private List<ServiceExtensionProxy.ExtensionLink> chain;
//
//    @SuppressWarnings("unchecked")
//    private Class<T> nextClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public AbstractServiceExtension() {
    }

//    public AbstractServiceExtension(Class<T> nextClass) {
//        this.nextClass = nextClass;
//    }


    void setChain(List<ServiceExtensionProxy.ExtensionLink> chain) {
        this.chain = chain;
    }

    void setProxyController(P proxyController) {
        this.proxyController = proxyController;
    }

    public final T getNext() {
        return proxyController.getNext(this);
    }

    public P getProxyController() {
        return proxyController;
    }
}
