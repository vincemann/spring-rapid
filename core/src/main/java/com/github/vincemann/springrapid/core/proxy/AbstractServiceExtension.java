package com.github.vincemann.springrapid.core.proxy;


public class AbstractServiceExtension<T,P extends ProxyController>
        implements NextLinkAware<T>{

    private ChainController<T> chain;
    private P proxyController;

    public AbstractServiceExtension() {
    }


    public void setProxyController(P proxyController) {
        this.proxyController = proxyController;
    }

    void setChain(ChainController<T> chain) {
        this.chain = chain;
    }

    public T getNext() {
        return chain.getNext(this);
    }

    //can be safely casted to crudservice
    public T getLast(){
        return chain.getLast();
    }

    protected P getProxyController() {
        return proxyController;
    }
}
