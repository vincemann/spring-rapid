package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.IBeanNameAware;
import com.google.common.base.Objects;

public abstract class AbstractServiceExtension<T>
        implements NextLinkAware<T>,AopLoggable, IBeanNameAware {


    private String beanName;
    private Chain chain;


    public AbstractServiceExtension() {
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
        AbstractServiceExtension<?> that = (AbstractServiceExtension<?>) o;
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
