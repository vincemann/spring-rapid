package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclProperties;
import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ProxyController;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.AopTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Adds default acl checks extension to all @{@link Secured} proxies.
 */
@Slf4j
@ServiceConfig
public class RapidDefaultSecurityExtensionAutoConfiguration {

    @Autowired
    ApplicationContext context;


//    private List<AbstractServiceExtension> defaultSecurityExtensions;
//
//    @Autowired(required = false)
//    @DefaultSecurityExtension
//    public void setDefaultExtensions(List<AbstractServiceExtension> defaultExtensions) {
//        this.defaultSecurityExtensions = defaultExtensions;
//    }

    //    @ConditionalOnProperty(prefix = "rapid-acl", name = "defaultAclChecks")
    @Autowired
    public void addDefaultSecurityExtension(AclProperties aclProperties, @Secured List<CrudService> securityProxies, @Autowired(required = false) @DefaultSecurityExtension Optional<List<AbstractServiceExtension>> defaultSecurityExtensionsOptional){

//        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
//        Qualifier qualifier = AnnotationUtils.findAnnotation(Secured.class, Qualifier.class);
//        Collection<CrudService> securityProxies = BeanFactoryAnnotationUtils.qualifiedBeansOfType((ListableBeanFactory) beanFactory, CrudService.class, qualifier.value()).values();


//        List<AbstractServiceExtension> defaultSecurityExtensions = new ArrayList<>(defaultSecurityExtensionsOptional.orElse(Collections.emptyList()));
//
//        if (aclProperties.isDefaultAclChecks()){
////            AbstractServiceExtension crudChecksExtension = defaultSecurityExtensions.stream().filter(e -> e.getClass().equals(CrudAclChecksSecurityExtension.class)).findFirst().get();
////            AbstractServiceExtension crudCreatePermOnParentExtension = defaultSecurityExtensions.stream().filter(e -> e.getClass().equals(NeedCreatePermissionOnParentForSaveExtension.class)).findFirst().get();
////            defaultSecurityExtensions.remove(crudChecksExtension);
////            defaultSecurityExtensions.remove(crudCreatePermOnParentExtension);
//            AbstractServiceExtension<?, ? super ProxyController> crudAclChecksExtension = (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("crudAclChecksSecurityExtension");
////            AbstractServiceExtension<?, ? super ProxyController> parentCreatePermissionCheckExtension = (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("needCreatePermissionOnParentForSaveExtension");
//            defaultSecurityExtensions.add(crudAclChecksExtension);
////            defaultSecurityExtensions.add(parentCreatePermissionCheckExtension);
//        }

        for (CrudService securityProxy : securityProxies) {
            ExtensionProxy proxy = ProxyUtils.getExtensionProxy(securityProxy);
            if (!proxy.getDefaultExtensionsEnabled()){
                log.debug("Default security extensions disabled for proxy: " + proxy);
                continue;
            }

            List<AbstractServiceExtension> defaultSecurityExtensions = createDefaultSecurityExtensions(defaultSecurityExtensionsOptional);
            log.debug("Adding Default security extensions for proxy: " + proxy);
            for (AbstractServiceExtension extension : defaultSecurityExtensions) {
                if (proxy.isIgnored(AopTestUtils.getUltimateTargetObject(extension).getClass())){
                    log.info("ignoring default extension: " + extension.getClass().getSimpleName());
                    continue;
                }
                proxy.addExtension(extension);
            }
        }
    }

    /**
     * scope of extensions has to be Prototype, because I need a new instance for each proxy of that extension.
     * Call this method for each proxy to get a new set of instances of the default extensions.
     */
    private List<AbstractServiceExtension> createDefaultSecurityExtensions(Optional<List<AbstractServiceExtension>> defaultSecurityExtensionsOptional) {
        List<AbstractServiceExtension> defaultSecurityExtensions = new ArrayList<>(defaultSecurityExtensionsOptional.orElse(Collections.emptyList()));

        // stores new instances of extensions, that can be added by proxy
        List<AbstractServiceExtension> extensions = new ArrayList<>();
        for (AbstractServiceExtension extension : defaultSecurityExtensions) {
            String beanName = context.getBeanNamesForType(extension.getClass())[0];
            // get new instance
            AbstractServiceExtension defaultExtension = (AbstractServiceExtension) context.getBean(beanName);
            extensions.add(defaultExtension);
        }
        return extensions;
    }
}
