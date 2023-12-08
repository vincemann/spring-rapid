package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclProperties;
import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ProxyController;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * Adds default acl checks extension to all @{@link Secured} proxies.
 */
@Slf4j
@ServiceConfig
public class RapidDefaultSecurityExtensionAutoConfiguration {

    @Autowired
    ApplicationContext context;

//    @ConditionalOnProperty(prefix = "rapid-acl", name = "defaultAclChecks")
    @Autowired
    public void addDefaultSecurityExtension(AclProperties aclProperties, @Secured List<CrudService> securityProxies, @DefaultSecurityExtension List<AbstractServiceExtension> defaultSecurityExtensions){

//        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
//        Qualifier qualifier = AnnotationUtils.findAnnotation(Secured.class, Qualifier.class);
//        Collection<CrudService> securityProxies = BeanFactoryAnnotationUtils.qualifiedBeansOfType((ListableBeanFactory) beanFactory, CrudService.class, qualifier.value()).values();

        if (aclProperties.isDefaultAclChecks()){
            AbstractServiceExtension<?, ? super ProxyController> crudAclChecksExtension = (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("crudAclChecksSecurityExtension");
            defaultSecurityExtensions.add(crudAclChecksExtension);
        }

        for (CrudService securityProxy : securityProxies) {
            ServiceExtensionProxy proxy = ProxyUtils.getExtensionProxy(securityProxy);
            if (!proxy.getDefaultExtensionsEnabled()){
                log.debug("Default security extensions disabled for proxy: " + proxy);
                continue;
            }
            log.debug("Adding Default security extensions for proxy: " + proxy);
            for (AbstractServiceExtension extension : defaultSecurityExtensions) {
                proxy.addExtension(extension);
            }
        }
    }
}
