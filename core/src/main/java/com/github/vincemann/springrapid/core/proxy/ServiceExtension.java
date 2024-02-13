package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.IBeanNameAware;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import com.github.vincemann.springrapid.core.util.TypeResolver;
import com.google.common.base.Objects;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public abstract class ServiceExtension<T>
        implements NextLinkAware<T>,AopLoggable, IBeanNameAware {


    private String beanName;
    private Chain chain;


    @Setter
    private Class<?> targetClass;



    public ServiceExtension() {
        this.targetClass = findTargetClass();
    }

    protected Class<?> findTargetClass(){
        Class<?> clazz = TypeResolver.findFirstGenericParameter(this.getClass());
        if (clazz == null){
            log.debug("could not find type parameter, call setter for target class on extension or just ignore - but dynamic type safety check is ignored for adding extensions to proxy");
        }
        return clazz;
    }

    protected void setChain(Chain chain) {
        this.chain = chain;
    }

    protected Chain getChain() {
        return chain;
    }

    /**
     * call this method in order to find out dynamically if you can add this extension to the given proxy
     * -> kinda like dynamic type safety
     */
    public boolean matchesProxy(Object proxy){
        ExtensionProxy p = ProxyUtils.getExtensionProxy(proxy);
        return matchesProxy(p);
    }

    public boolean matchesProxy(ExtensionProxy proxy){
        if (targetClass == null){
            log.debug("target class is null, ignoring dynamic type safety check");
            return true;
        }
        return targetClass.isAssignableFrom(proxy.getProxied().getClass());
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
        ServiceExtension<?> that = (ServiceExtension<?>) o;
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
