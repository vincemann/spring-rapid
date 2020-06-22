package com.github.vincemann.springrapid.core.proxy;


import com.github.vincemann.springrapid.commons.Lists;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

public class CrudServicePluginProxyFactory {

    /**
     * Creates a {@link CrudServicePluginProxy} of @param service with given {@link CrudServicePlugin}s.
     * The plugins will be called in the order you give them to this Factory.
     */
    public static <Id extends Serializable, E extends IdentifiableEntity<Id>, S extends CrudService<E, Id, ? extends CrudRepository<E, Id>>> S
    create(S service, CrudServicePlugin<? super E, ? super Id>... plugins) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(service.getClass());
        enhancer.setCallback(new CrudServicePluginProxy(service,Lists.newArrayList(plugins)));
        return  (S) enhancer.create();

//        //resolve spring aop proxy
//        S unproxied = AopTestUtils.getUltimateTargetObject(service);
//        S proxyInstance = (S) Proxy.newProxyInstance(
//                unproxied.getClass().getClassLoader(),
//                ClassUtils.getAllInterfaces(unproxied.getClass()).toArray(new Class[0]),
//                new CrudServicePluginProxy(unproxied, Lists.newArrayList(plugins)));
//        return proxyInstance;
    }


}
