package com.github.vincemann.springrapid.acl.config;


import com.github.vincemann.springrapid.acl.AclProperties;
import com.github.vincemann.springrapid.acl.DefaultAclExtension;
import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.service.extensions.acl.AdminHasFullPermissionAboutSavedAclExtension;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ProxyController;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;

@Slf4j
@ServiceConfig
public class RapidDefaultAclExtensionsAutoConfiguration {

    @Autowired
    ApplicationContext context;

    @Autowired
    public void addDefaultAclExtension(AclProperties aclProperties, @Acl List<CrudService> aclProxies, @DefaultAclExtension List<AbstractServiceExtension> defaultAclExtensions) {
//        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
//        Qualifier qualifier = AnnotationUtils.findAnnotation(Acl.class, Qualifier.class);
//        Collection<CrudService> aclProxies = BeanFactoryAnnotationUtils.qualifiedBeansOfType((ListableBeanFactory) beanFactory, CrudService.class, qualifier.value()).values();

        if (aclProperties.isAdminFullAccess()){
            AbstractServiceExtension<?, ? super ProxyController> adminExtension = (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("adminHasFullPermissionAboutSavedAclExtension");
            defaultAclExtensions.add(adminExtension);
        }
        if (aclProperties.isAdminFullAccess()){
            AbstractServiceExtension<?, ? super ProxyController> cleanUpAclExtension = (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("cleanUpAclExtension");
            defaultAclExtensions.add(cleanUpAclExtension);
        }
        for (CrudService aclProxy : aclProxies) {
            ServiceExtensionProxy proxy = ProxyUtils.getExtensionProxy(aclProxy);
            if (!proxy.getDefaultExtensionsEnabled()){
                log.debug("Default acl extensions disabled for proxy: " + proxy);
                continue;
            }
            log.debug("Adding Default acl extensions for proxy: " + proxy);
            for (AbstractServiceExtension defaultAclExtension : defaultAclExtensions) {
                proxy.addExtension(defaultAclExtension);
            }
        }
    }

}
