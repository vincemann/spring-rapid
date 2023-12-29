package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxy;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.Set;


@ServiceComponent
public abstract class AbstractCrudService
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E, Id>
                >
        implements CrudService<E, Id>, TargetClassAware {
    private String beanName;
    private R repository;
//    protected CrudService<E, Id> service;
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        // this works - gives me proxied instance with aop working
//        this.service = applicationContext.getBean(this.getClass());
////        System.err.println("Initializing this: " + this + " with instance: " + this.service);
//    }


    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public R getRepository() {
        return repository;
    }

    @Autowired
    public void injectRepository(R repository) {
        this.repository = repository;
    }


    // will not be wrapped with right proxy -> no aop possible


    @Override
    public Class<E> getEntityClass() {
        return entityClass;
    }

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