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
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@ServiceConfig
@AutoConfigureBefore(RapidDefaultSecurityExtensionAutoConfiguration.class)
public class RapidDefaultAclExtensionsAutoConfiguration {

    @Autowired
    ApplicationContext context;

    @Autowired
    public void addDefaultSecurityExtension(AclProperties aclProperties,  @Acl Set<CrudService> aclProxies) {
//        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
//        Qualifier qualifier = AnnotationUtils.findAnnotation(Acl.class, Qualifier.class);
//        Collection<CrudService> aclProxies = BeanFactoryAnnotationUtils.qualifiedBeansOfType((ListableBeanFactory) beanFactory, CrudService.class, qualifier.value()).values();

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
