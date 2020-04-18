package io.github.vincemann.springrapid.core.proxy.factory;

import io.github.vincemann.springrapid.core.util.Lists;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.proxy.invocationHandler.CrudServicePluginProxy;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.core.service.plugin.CrudServicePlugin;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                unproxied.getClass().getClassLoader(), getAllInterfaces(unproxied.getClass()),
                new CrudServicePluginProxy(unproxied, Lists.newArrayList(plugins)));

        return proxyInstance;
    }


    //whole class hierachy
    private static Class[] getAllInterfaces(Class clazz) {
        Class curr = clazz;
        Set<Class> interfaces = new HashSet<>();
        do {
            interfaces.addAll(Lists.newArrayList(curr.getInterfaces()));
            curr = curr.getSuperclass();
        } while (!curr.equals(Object.class));
        interfaces.forEach(i -> interfaces.addAll(Lists.newArrayList(i.getInterfaces())));
        return interfaces.toArray(new Class[0]);
    }


}
