package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogConfig;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.google.common.base.Objects;

// dont log extensions methods -> otherwise you will have log for i.E. save 3 times for 2 extensions..
// explicitly log methods you want to log
@LogInteraction(disabled = true)
@LogConfig(logAllChildrenMethods = true)
public class AbstractServiceExtension<T,P extends ProxyController>
        implements NextLinkAware<T>, AopLoggable {

    private ChainController<T> chain;
    private P proxyController;

    public AbstractServiceExtension() {
    }


    protected void setProxyController(P proxyController) {
        this.proxyController = proxyController;
    }

    protected void setChain(ChainController<T> chain) {
        this.chain = chain;
    }

    protected ChainController<T> getChain() {
        return chain;
    }

    public T getNext() {
        return chain.getNext(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractServiceExtension<?, ?> that = (AbstractServiceExtension<?, ?>) o;
        return Objects.equal(getChain(), that.getChain()) &&
                Objects.equal(getProxyController(), that.getProxyController());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getChain(), getProxyController());
    }

    //can be safely casted to crudservice
    protected T getLast(){
        return chain.getLast();
    }

    protected P getProxyController() {
        return proxyController;
    }
}
