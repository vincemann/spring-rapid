package com.github.vincemann.springrapid.acl.config;


import com.github.vincemann.springrapid.acl.AclProperties;
import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ProxyController;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.ApplicationContext;

import java.util.List;

@Slf4j
@ServiceConfig
@AutoConfigureBefore(RapidDefaultSecurityExtensionAutoConfiguration.class)
public class RapidDefaultAclExtensionsAutoConfiguration {

    @Autowired
    ApplicationContext context;

    @Autowired
    public void addDefaultSecurityExtension(AclProperties aclProperties, @Acl List<CrudService> aclProxies) {
        for (CrudService aclProxy : aclProxies) {
            ServiceExtensionProxy proxy = ProxyUtils.getExtensionProxy(aclProxy);
            if (proxy.getDisableDefaultExtensions()){
                log.debug("Default acl extensions disabled for proxy: " + proxy);
                continue;
            }
            if (aclProperties.isAdminFullAccess()) {
                log.debug("Adding Default acl extensions for proxy: " + proxy);
                proxy.addExtension(
                        (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("adminFullAccessAclExtension")
                );
            }
            if (aclProperties.isCleanupAcl()) {
                proxy.addExtension(
                        (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("cleanUpAclExtension")
                );
            }
        }
        
    }
}
