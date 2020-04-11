package io.github.vincemann.springrapid.acl.proxy.create;

import io.github.vincemann.springrapid.acl.proxy.CrudServiceSecurityProxy;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.CrudService;
import io.github.vincemann.springrapid.acl.proxy.rules.DefaultServiceSecurityRule;
import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.acl.securityChecker.SecurityChecker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.test.util.AopTestUtils;

import java.io.Serializable;
import java.lang.reflect.Proxy;

@Component
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
                unproxied.getClass().getClassLoader(), unproxied.getClass().getInterfaces(),
                new CrudServiceSecurityProxy(unproxied, securityChecker, defaultServiceSecurityRule,rules));
        return proxyInstance;
    }
}
