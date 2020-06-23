package com.github.vincemann.springlemon.auth.service;

import com.github.vincemann.springlemon.auth.config.LemonServiceAutoConfiguration;
import com.github.vincemann.springrapid.acl.proxy.CrudServiceSecurityProxy;
import com.github.vincemann.springrapid.acl.service.AclManaging;
import com.github.vincemann.springrapid.acl.service.Secured;
import com.github.vincemann.springrapid.core.proxy.CrudServicePluginProxy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.AopTestUtils;

import java.lang.reflect.Proxy;

/**
 * Extend this class to further configure PreConfigured {@link LemonService} Proxies.
 * Used primarily to add more plugins.
 *
 * @see LemonServiceAutoConfiguration
 */
public abstract class LemonServiceProxyConfigurer  implements InitializingBean {

    private LemonService acl;
    private LemonService secured;

    @Autowired
    @AclManaging
    public void setAcl(LemonService acl) {
        this.acl=acl;
    }

    @Autowired
    @Secured
    public void setSecured(LemonService secured) {
        this.secured = secured;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        configureAclManaging((CrudServicePluginProxy) Proxy.getInvocationHandler(AopTestUtils.getUltimateTargetObject(acl)));
        configureSecured((CrudServiceSecurityProxy) Proxy.getInvocationHandler(AopTestUtils.getUltimateTargetObject(secured)));
    }

    public void configureAclManaging(CrudServicePluginProxy proxy){}
    public void configureSecured(CrudServiceSecurityProxy proxy){}
}
