package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.acl.proxy.rules.DefaultServiceSecurityRule;
import com.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import com.github.vincemann.springrapid.acl.SecurityChecker;
<<<<<<< HEAD:acl/src/main/java/com/github/vincemann/springrapid/acl/proxy/CrudServiceSecurityProxyFactory.java
import org.apache.commons.lang3.ClassUtils;
=======
import net.sf.cglib.proxy.Enhancer;
>>>>>>> c1a78c3618c18ae347e1d29c9c4f807fcc0b7547:acl/src/main/java/com/github/vincemann/springrapid/acl/proxy/CrudServiceSecurityProxyFactory.java
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

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
    create(S service, ServiceSecurityRule... rules) {
//        S unproxied = AopTestUtils.getTargetObject(service);
//        S proxyInstance = (S) Proxy.newProxyInstance(
//                unproxied.getClass().getClassLoader(),
//                ClassUtils.getAllInterfaces(unproxied.getClass()).toArray(new Class[0]),
//                new CrudServiceSecurityProxy(unproxied, securityChecker, defaultServiceSecurityRule,rules));
//        return proxyInstance;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(service.getClass());
        enhancer.setCallback(new CrudServiceSecurityProxy(service, securityChecker,defaultServiceSecurityRule,rules));
        return  (S) enhancer.create();
    }
}
