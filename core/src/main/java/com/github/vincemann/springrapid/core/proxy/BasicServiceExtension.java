package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.IBeanNameAware;
import com.google.common.base.Objects;

/**
 * Write extensions for normal Services, managed by {@link ExtensionProxy}.
 *
 * Create your own Service Extension by extending from this class and implementing the interface(s) of the service, that you want
 * to write an Extension for.
 * {@link ExtensionProxy} will call the extension, when given to it, and integrate it in the extension chain.
 *
 *
 * @param <T> Type of proxied Service, you are writing the Extension for.
 */
public class BasicServiceExtension<T>
        implements NextLinkAware<T>,AopLoggable, IBeanNameAware {


    private String beanName;
    private Chain chain;


    public BasicServiceExtension() {
    }

    protected void setChain(Chain chain) {
        this.chain = chain;
    }

    protected Chain getChain() {
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
        return (T) getChain().getNext(this);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicServiceExtension<?> that = (BasicServiceExtension<?>) o;
        return Objects.equal(getChain(), that.getChain());
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
        return Objects.hashCode(getChain());
    }

    //can be safely casted to crudservice
    protected T getLast(){
        return (T) getChain().getLast();
    }

}
