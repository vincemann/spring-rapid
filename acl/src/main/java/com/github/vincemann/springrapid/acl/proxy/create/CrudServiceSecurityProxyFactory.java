package com.github.vincemann.springrapid.acl.proxy.create;

import com.github.vincemann.springrapid.acl.proxy.CrudServiceSecurityProxy;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.acl.proxy.rules.DefaultServiceSecurityRule;
import com.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import com.github.vincemann.springrapid.acl.securityChecker.SecurityChecker;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;

public class CrudServiceSecurityProxyFactory {

    private SecurityChecker securityChecker;
    private ServiceSecurityRule defaultServiceSecurityRule;

    public CrudServiceSecurityProxyFactory(SecurityChecker securityChecker,
                                           @DefaultServiceSecurityRule ServiceSecurityRule defaultServiceSecurityRule
    ) {
        this.securityChecker = securityChecker;
        this.defaultServiceSecurityRule = defaultServiceSecurityRule;
    }

    //we need the class explicitly here to avoid issues with other proxies. HibernateProxies for example, are not interfaces, so service.getClass returns no interface
    //-> this would make this crash
    public <Id extends Serializable, E extends IdentifiableEntity<Id>, S extends CrudService<E, Id, ? extends CrudRepository<E, Id>>> S
    create(S crudService, ServiceSecurityRule... rules) {
        S unproxied = AopTestUtils.getTargetObject(crudService);
        S proxyInstance = (S) Proxy.newProxyInstance(
                unproxied.getClass().getClassLoader(),
                ClassUtils.getAllInterfaces(unproxied.getClass()).toArray(new Class[0]),
                new CrudServiceSecurityProxy(unproxied, securityChecker, defaultServiceSecurityRule,rules));
        return proxyInstance;
    }
}
