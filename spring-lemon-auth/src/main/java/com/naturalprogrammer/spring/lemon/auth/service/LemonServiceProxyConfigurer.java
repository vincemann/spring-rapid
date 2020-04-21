package com.naturalprogrammer.spring.lemon.auth.service;

import io.github.vincemann.springrapid.acl.proxy.CrudServiceSecurityProxy;
import io.github.vincemann.springrapid.acl.service.AclManaging;
import io.github.vincemann.springrapid.acl.service.Secured;
import io.github.vincemann.springrapid.core.proxy.invocationHandler.CrudServicePluginProxy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Extend this class to further configure PreConfigured and created {@link LemonService}s.
 * Used primarily to add more plugins.
 *
 * @see com.naturalprogrammer.spring.lemon.auth.config.LemonServiceAutoConfiguration
 */
public abstract class LemonServiceProxyConfigurer {

    @Autowired
    @AclManaging
    public void setAcl(LemonService acl) {
        configureAclManaging(((CrudServicePluginProxy) acl));
    }

    @Autowired
    @Secured
    public void setSecured(LemonService secured) {
        configureSecured(((CrudServiceSecurityProxy) secured));
    }


    public void configureAclManaging(CrudServicePluginProxy proxy){}
    public void configureSecured(CrudServiceSecurityProxy proxy){}
}
