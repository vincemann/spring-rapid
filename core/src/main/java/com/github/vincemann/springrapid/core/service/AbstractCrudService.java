package com.github.vincemann.springrapid.core.service;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxy;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;


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


    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public R getRepository() {
        return repository;
    }

    @Autowired
    public void injectRepository(R repository) {
        this.repository = repository;
    }

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
        return getBeanName();
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