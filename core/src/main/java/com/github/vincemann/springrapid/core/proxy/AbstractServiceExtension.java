package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.springrapid.core.service.CrudService;

public class AbstractServiceExtension<T,P extends ProxyController>
        implements NextLinkAware<T>{

    private ChainController<T> chain;
    private P proxyController;
//
//    @SuppressWarnings("unchecked")
//    private Class<T> nextClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public AbstractServiceExtension() {
    }

//    public AbstractServiceExtension(Class<T> nextClass) {
//        this.nextClass = nextClass;
//    }


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
