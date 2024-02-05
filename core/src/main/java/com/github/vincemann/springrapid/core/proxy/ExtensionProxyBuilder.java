package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;

/**
 * typesafe builder for {@link ExtensionProxy}
 * @param <S>   type of root service to proxy (must be {@link CrudService})
 * @param <E>   type of entity of root service
 * @param <Id>  type of id of entity
 */
public class ExtensionProxyBuilder<S extends CrudService<E,Id>,E extends IdentifiableEntity<Id>, Id extends Serializable> {
    private final ExtensionProxy proxy;

    public ExtensionProxyBuilder(S proxied) {
        this.proxy = new ExtensionProxy(proxied);
    }

//    public static <S extends SimpleCrudService<E,Id>,E extends IdentifiableEntity<Id>, Id extends Serializable> ServiceExtensionProxyBuilder<S,E,Id> builder(S proxied){
//        return new ServiceExtensionProxyBuilder<>(proxied);
//    }
//
//    public ServiceExtensionProxyBuilder(S proxy) {
//        this.proxy = proxy;
//    }

    //diese aufsplittung muss ich machen weil ich nicht sagen kann <T super S | T extends SimpleCrudService<? super E, ? super Id>>
    //das 'oder' wird durch 2 seperate methoden realisiert

    // this method is used to add SimpleService implementing extensions, to ensure down casting works
    // service extension can either be superclass, same class or child class of S, the only thing that matters, is that I can cast
    // from E to extension entity type. i.E. I can cast IdentEntity to Owner -> ? super E aka ? super Owner is correct


    /**
     * User this method if your {@link ServiceExtension} implements {@link GenericCrudServiceExtension}.
     */
    public ExtensionProxyBuilder<S,E,Id> addGenericExtensions(ServiceExtension<? extends CrudService<? super E,? super Id>>... extensions){
        for (ServiceExtension<? extends CrudService<? super E, ? super Id>> extension : extensions) {
            addGenericExtension(extension);
        }
        return this;
    }

    public ExtensionProxyBuilder<S,E,Id> addGenericExtension(ServiceExtension<? extends CrudService<? super E,? super Id>> extension){
        proxy.addExtension(extension);
        return this;
    }

    // service extension can be any super class of service
    // also types that are not of type SimpleService
    public ExtensionProxyBuilder<S,E,Id> addExtensions(ServiceExtension<? super S>... extensions){
        for (ServiceExtension<? super S> extension : extensions) {
            proxy.addExtension(extension);
        }
        return this;
    }

    public ExtensionProxyBuilder<S,E,Id> addExtension(ServiceExtension<? super S> extension){
        proxy.addExtension(extension);
        return this;
    }

    public ExtensionProxyBuilder<S,E,Id> ignoreDefaultExtensions(Class<? extends ServiceExtension>... extensions){
        for (Class<? extends ServiceExtension> extension : extensions) {
            proxy.ignoreExtension(extension);
        }
        return this;
    }


    public ExtensionProxyBuilder<S,E,Id> setDefaultExtensionsEnabled(Boolean enabled){
//        if (enabled==null){
//            enabled=true;
//        }
        proxy.setDefaultExtensionsEnabled(enabled);
        return this;
    }


    public S build(){
        S unproxied = AopTestUtils.getUltimateTargetObject(proxy.getProxied());
        S proxyInstance = (S) Proxy.newProxyInstance(
                unproxied.getClass().getClassLoader(),
                ClassUtils.getAllInterfaces(unproxied.getClass()).toArray(new Class[0]),
                proxy);
        return proxyInstance;
    }




}
