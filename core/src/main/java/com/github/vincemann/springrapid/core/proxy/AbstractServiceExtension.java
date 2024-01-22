package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.IBeanNameAware;
import com.google.common.base.Objects;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class AbstractServiceExtension<T,P extends ProxyController>
        implements NextLinkAware<T>,AopLoggable, IBeanNameAware {

    private static ThreadLocal<Map<String, Object>> threadLocalCache = ThreadLocal.withInitial(HashMap::new);

    protected static void putInThreadLocalCache(String key, Object value) {
        Map<String, Object> cache = threadLocalCache.get();
        cache.put(key, value);
    }

    protected static Object getFromThreadLocalCache(String key) {
        Map<String, Object> cache = threadLocalCache.get();
        return cache.get(key);
    }

    protected static void removeFromThreadLocalCache(String key) {
        Map<String, Object> cache = threadLocalCache.get();
        cache.remove(key);
    }

    public <O> O cache(Callable<O> callable, String key) throws Exception {
        Object cached = getFromThreadLocalCache(key);
        if (cached != null)
            return (O) cached;
        O result = callable.call();
        putInThreadLocalCache(key,result);
        return result;
    }


    private String beanName;
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
     * e.g.
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
        return getChain().getNext(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractServiceExtension<?, ?> that = (AbstractServiceExtension<?, ?>) o;
        return Objects.equal(getChain(), that.getChain()) &&
                Objects.equal(getProxyController(), that.getProxyController());
    }

    // todo change
    // only implements this interface, bc services do in order to log beanNames instead of only class-method
    // but logging bean name for extenions makes no sense aka leads to confusion
    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(getChain(), getProxyController());
    }

    //can be safely casted to crudservice
    protected T getLast(){
        return getChain().getLast();
    }

    protected P getProxyController() {
        return proxyController;
    }
}
