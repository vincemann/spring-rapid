package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;

public class SecurityServiceProxyFactory {

    private SecurityServiceExtension defaultServiceSecurityExtension;

    public SecurityServiceProxyFactory(@DefaultSecurityServiceExtension SecurityServiceExtension defaultServiceSecurityExtension
    ) {
        this.defaultServiceSecurityExtension = defaultServiceSecurityExtension;
    }

    //we need the class explicitly here to avoid issues with other proxies. HibernateProxies for example, are not interfaces, so service.getClass returns no interface
    //-> this would make this crash
    public <Id extends Serializable, E extends IdentifiableEntity<Id>, S extends CrudService<E, Id, ? extends CrudRepository<E, Id>>> S
    create(S crudService, SecurityServiceExtension... rules) {
        S unproxied = AopTestUtils.getTargetObject(crudService);
        S proxyInstance = (S) Proxy.newProxyInstance(
                unproxied.getClass().getClassLoader(),
                ClassUtils.getAllInterfaces(unproxied.getClass()).toArray(new Class[0]),
                new SecurityExtensionServiceProxy(unproxied, defaultServiceSecurityExtension,rules));
        return proxyInstance;
    }
}
