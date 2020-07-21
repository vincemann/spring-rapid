package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;

public class SecurityServiceExtensionProxyBuilder<S extends SimpleCrudService<E,Id>,E extends IdentifiableEntity<Id>, Id extends Serializable> {

    private SecurityExtensionServiceProxy<S> proxy;
    private SecurityServiceExtension<?> defaultSecurityExtension;


    protected SecurityServiceExtensionProxyBuilder(S proxied,SecurityServiceExtension<?> defaultSecurityExtension){
        this.proxy= new SecurityExtensionServiceProxy<>(proxied);
        this.defaultSecurityExtension = defaultSecurityExtension;
    }


    //diese aufsplittung muss ich machen weil ich nicht sagen kann <T super S | T extends SimpleCrudService<? super E, ? super Id>>
    //das oder wird durch 2 seperate methoden realisiert

    // this method is used to add SimpleService implementing extensions, to ensure down casting works
    // service extension can either be superclass, same class or child class of S, the only thing that matters, is that I can cast
    // from E to extension entity type. i.E. I can cast IdentEntity to Owner -> ? super E aka ? super Owner is correct
    public SecurityServiceExtensionProxyBuilder<S,E,Id> addServiceExtensions(SecurityServiceExtension<? extends SimpleCrudService<? super E,? super Id>>... extensions){
        for (SecurityServiceExtension<? extends SimpleCrudService<? super E, ? super Id>> extension : extensions) {
            proxy.addExtension(extension);
        }
        return this;
    }

    // service extension can be any super class of service
    // also types that are not of type SimpleService
    public SecurityServiceExtensionProxyBuilder<S,E,Id> addSuperExtensions(SecurityServiceExtension<? super S>... extensions){
        for (SecurityServiceExtension<? super S> extension : extensions) {
            proxy.addExtension(extension);
        }
        return this;
    }


    public S build(){
        this.proxy.addExtension(defaultSecurityExtension);
        this.proxy.setDefaultExtension(defaultSecurityExtension);
        S unproxied = AopTestUtils.getUltimateTargetObject(proxy.getProxied());
        S proxyInstance = (S) Proxy.newProxyInstance(
                unproxied.getClass().getClassLoader(),
                ClassUtils.getAllInterfaces(unproxied.getClass()).toArray(new Class[0]),
                proxy);
        return proxyInstance;
    }
}
