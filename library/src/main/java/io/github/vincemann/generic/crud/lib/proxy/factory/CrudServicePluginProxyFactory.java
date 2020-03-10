package io.github.vincemann.generic.crud.lib.proxy.factory;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.proxy.invocationHandler.PluginCrudServiceProxy;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.plugin.CrudServicePlugin;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class CrudServicePluginProxyFactory {
    //we need the class explicitly here to avoid issues with other proxies. HibernateProxies for example, are not interfaces, so service.getClass returns no interface
    //-> this would make this crash
    public static <Id extends Serializable, E extends IdentifiableEntity<Id>, S extends CrudService<E, Id, ? extends CrudRepository<E, Id>>> S
    create(S crudService, CrudServicePlugin<? super E, ? super Id>... plugins) {
        //resolve spring proxy
        S unproxied = AopTestUtils.getUltimateTargetObject(crudService);
        S proxyInstance = (S) Proxy.newProxyInstance(
                unproxied.getClass().getClassLoader(), unproxied.getClass().getInterfaces(),
                new PluginCrudServiceProxy(unproxied, Arrays.asList(plugins)));

        return proxyInstance;
    }


}
