package com.github.vincemann.springrapid.core.proxy.factory;


import com.github.vincemann.springrapid.commons.Lists;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.invocationHandler.CrudServicePluginProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.proxy.invocationHandler.CrudServicePlugin;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;

public class CrudServicePluginProxyFactory {
    //we need the class explicitly here to avoid issues with other proxies. HibernateProxies for example, are not interfaces, so service.getClass returns no interface
    //-> this would make this crash

    /**
     * Creates a {@link CrudServicePluginProxy} of @param crudService with given {@link CrudServicePlugin}s.
     * The plugins will be called in the order you give them to this Factory.
     */
    public static <Id extends Serializable, E extends IdentifiableEntity<Id>, S extends CrudService<E, Id, ? extends CrudRepository<E, Id>>> S
    create(S crudService, CrudServicePlugin<? super E, ? super Id>... plugins) {
        //resolve spring aop proxy
        S unproxied = AopTestUtils.getUltimateTargetObject(crudService);
        S proxyInstance = (S) Proxy.newProxyInstance(
                unproxied.getClass().getClassLoader(),
                ClassUtils.getAllInterfaces(unproxied.getClass()).toArray(new Class[0]),
                new CrudServicePluginProxy(unproxied, Lists.newArrayList(plugins)));

        return proxyInstance;
    }


}
