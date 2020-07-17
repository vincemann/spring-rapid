package com.github.vincemann.springrapid.core.proxy;


public class AbstractServiceExtension<T,P extends ChainController> {
    private P chain;
//
//    @SuppressWarnings("unchecked")
//    private Class<T> nextClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public AbstractServiceExtension() {
    }

//    public AbstractServiceExtension(Class<T> nextClass) {
//        this.nextClass = nextClass;
//    }



    void setChain(P chain) {
        this.chain = chain;
    }

    public final T getNext() {
        return chain.getNext(this);
    }

    public P getChain() {
        return chain;
    }
}
