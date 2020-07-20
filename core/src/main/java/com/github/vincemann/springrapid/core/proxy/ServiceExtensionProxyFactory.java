package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.SimpleCrudService;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;

public class ServiceExtensionProxyFactory {
    //we need the class explicitly here to avoid issues with other proxies. HibernateProxies for example, are not interfaces, so service.getClass returns no interface
    //-> this would make this crash

    /**
     * Creates a {@link ExtensionServiceProxy} of @param crudService with given {@link ServiceExtension}s.
     * The extensions will be called in the order you give them to this Factory.
     */
    public static <E extends IdentifiableEntity<Id>,Id extends Serializable, S extends SimpleCrudService<E,Id>> S
    create(S crudService, ServiceExtension<? super S>... extensions) {
        //resolve spring aop proxy
        S unproxied = AopTestUtils.getUltimateTargetObject(crudService);
        S proxyInstance = (S) Proxy.newProxyInstance(
                unproxied.getClass().getClassLoader(),
                ClassUtils.getAllInterfaces(unproxied.getClass()).toArray(new Class[0]),
                new ExtensionServiceProxy(unproxied, extensions));
        return proxyInstance;
    }


}
