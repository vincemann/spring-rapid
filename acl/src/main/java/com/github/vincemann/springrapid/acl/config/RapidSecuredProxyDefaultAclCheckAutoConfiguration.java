package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;

/**
 * Adds default acl checks extension to all @{@link Secured} proxies.
 */
@ConditionalOnProperty(prefix = "rapid-acl", name = "defaultAclChecks")
@ServiceConfig
@AutoConfigureAfter(RapidSecurityProxyAutoConfiguration.class)
public class RapidSecuredProxyDefaultAclCheckAutoConfiguration {


    @Autowired
    public void addDefaultSecurityExtension(@Secured List<CrudService> securityProxies, @Qualifier("defaultAclChecksExtension") AbstractServiceExtension defaultAclChecksExtension){
        for (CrudService securityProxy : securityProxies) {
            ProxyUtils.getExtensionProxy(securityProxy).addExtension(defaultAclChecksExtension);
        }
    }
}
