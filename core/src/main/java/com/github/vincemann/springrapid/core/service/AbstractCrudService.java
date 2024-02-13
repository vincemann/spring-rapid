package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.util.TypeResolver;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.aop.TargetClassAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;


public abstract class AbstractCrudService
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E, Id>
                >
    // targetClassAware is needed for aop proxy stuff to work, in order to get the real class behind proxy
        implements CrudService<E, Id>, TargetClassAware, ApplicationContextAware {
    private String beanName;
    private R repository;
    // root version of service
    @Setter
    protected CrudService<E, Id> service;

    // todo for some reason this only works in the setter, not in afterPropertiesSet, fix
    // usually the proxy versions are not available yet, only the root version
    // if a proxy should be injected instead
    // implement InitializingBean::afterPropertiesSet and set the service to new instance
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.service = provideRootService(applicationContext);
    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        // inject root version (primary) of service
//        this.service = provideRootService(context);
//    }

    /**
     * Overwrite this to inject different root version
     */
    protected CrudService<E,Id> provideRootService(ApplicationContext applicationContext){
        // this works - gives me proxied instance with aop working
        return applicationContext.getBean(this.getClass());
    }


    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) TypeResolver.findFirstGenericParameter(this.getClass());

    public R getRepository() {
        return repository;
    }

    @Autowired
    public void setRepository(R repository) {
        this.repository = repository;
    }

    @Override
    public Class<E> getEntityClass() {
        return entityClass;
    }

    // todo fix beanname issue
    @Override
    public String getBeanName() {
//        if (!Proxy.isProxyClass(this.getClass())) {
//            System.err.println("not a proxy");
//        }
//        try {
//            ServiceExtensionProxy<AbstractCrudService<E, Id, R>> extensionProxy = ProxyUtils.getExtensionProxy(this);
//            return extensionProxy.getBeanName();
//        } catch (IllegalArgumentException e) {
//            return this.getBeanName();
//        }
        return this.beanName;
    }

    @Override
    public void setBeanName(String name) {
//        if (!Proxy.isProxyClass(this.getClass())) {
//            System.err.println("not a proxy");
//        }
//        try {
//            ServiceExtensionProxy<AbstractCrudService<E, Id, R>> extensionProxy = ProxyUtils.getExtensionProxy(this);
//            extensionProxy.setBeanName(name);
//        } catch (IllegalArgumentException e) {
//            this.beanName = name;
//        }
        this.beanName = name;
    }



}