package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogConfig;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.google.common.base.Objects;
import org.springframework.aop.TargetClassAware;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.test.util.AopTestUtils;

// dont log extensions methods -> otherwise you will have log for i.E. save 3 times for 2 extensions..
// explicitly log methods you want to log
//// todo this is not applied bc crudservice is closer in hierarchy so its config is used instead of this one
//@LogInteraction(disabled = true)
//@LogConfig(logAllChildrenMethods = true,ignoreGetters = true,ignoreSetters = true)
//dependencys will be injected by aspectj, you should create the extensions with new
//this is done, so the extensions are not in the container as duplicate beans for service interfaces
public abstract class AbstractServiceExtension<T,P extends ProxyController>
        implements NextLinkAware<T> {

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


    /**
     * Only use this to call the proxied method.
     * Result may not actually have all methods of Type T, but always has the callee method.
     * I.e.
     * you can do this:
     * public void save(Owner entity){
     *     getNext().save(entity);
     * }
     *
     * never do something like this:
     * public void save(Owner entity){
     *     getNext().diffMethod();
     * }
     *
     * @return
     */
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
