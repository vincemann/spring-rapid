package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclProperties;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ProxyController;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

/**
 * Adds default acl checks extension to all @{@link Secured} proxies.
 */
//@ConditionalOnProperty(prefix = "rapid-acl", name = "defaultAclChecks")
@ServiceConfig
@AutoConfigureAfter(RapidSecurityProxyAutoConfiguration.class)
public class RapidSecuredProxyDefaultAclCheckAutoConfiguration{

    @Autowired
    ApplicationContext context;

    @Autowired
    public void addDefaultSecurityExtension(AclProperties aclProperties, @Secured List<CrudService> securityProxies){
        if (aclProperties.isDefaultAclChecks()) {
            for (CrudService securityProxy : securityProxies) {
                ProxyUtils.getExtensionProxy(securityProxy).addExtension(
                        (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("simpleAclChecksExtension")
                );
            }
        }
    }
}
