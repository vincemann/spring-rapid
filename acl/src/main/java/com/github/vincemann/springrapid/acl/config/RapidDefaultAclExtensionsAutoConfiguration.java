package com.github.vincemann.springrapid.acl.config;


import com.github.vincemann.springrapid.acl.DefaultAclExtension;
import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;
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

@Slf4j
@ServiceConfig
public class RapidDefaultAclExtensionsAutoConfiguration {

    @Autowired
    ApplicationContext context;

//    private List<AbstractServiceExtension> defaultAclExtensions;
//
//    @Autowired(required = false)
//    @DefaultAclExtension
//    public void setDefaultAclExtensions(List<AbstractServiceExtension> defaultAclExtensions) {
//        this.defaultAclExtensions = defaultAclExtensions;
//    }

    @Autowired
    public void addDefaultAclExtension(@Acl List<CrudService> aclProxies, @Autowired(required = false) @DefaultAclExtension Optional<List<AbstractServiceExtension>> defaultAclExtensionsOptional) {
//        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
//        Qualifier qualifier = AnnotationUtils.findAnnotation(Acl.class, Qualifier.class);
//        Collection<CrudService> aclProxies = BeanFactoryAnnotationUtils.qualifiedBeansOfType((ListableBeanFactory) beanFactory, CrudService.class, qualifier.value()).values();


        for (CrudService aclProxy : aclProxies) {
            ExtensionProxy proxy = ProxyUtils.getExtensionProxy(aclProxy);
            if (!proxy.getDefaultExtensionsEnabled()){
                log.debug("Default acl extensions disabled for proxy: " + proxy);
                continue;
            }
            List<AbstractServiceExtension> defaultAclExtensions = createDefaultAclExtensions(defaultAclExtensionsOptional);
            log.debug("Adding Default acl extensions for proxy: " + proxy);
            for (AbstractServiceExtension defaultAclExtension : defaultAclExtensions) {
                if (proxy.isIgnored(AopTestUtils.getUltimateTargetObject(defaultAclExtension).getClass())){
                    log.info("ignoring default extension: " + defaultAclExtension.getClass().getSimpleName());
                    continue;
                }
                proxy.addExtension(defaultAclExtension);
            }
        }
    }

    /**
     * scope of extensions has to be Prototype, because I need a new instance for each proxy of that extension.
     * Call this method for each proxy to get a new set of instances of the default extensions.
     */
    private List<AbstractServiceExtension> createDefaultAclExtensions(Optional<List<AbstractServiceExtension>> defaultAclExtensionsOptional){
        List<AbstractServiceExtension> defaultAclExtensions = new ArrayList<>(defaultAclExtensionsOptional.orElse(Collections.emptyList()));

        // stores new instances of extensions, that can be added by proxy
        List<AbstractServiceExtension> extensions = new ArrayList<>();
        for (AbstractServiceExtension extension : defaultAclExtensions) {
            String beanName = context.getBeanNamesForType(extension.getClass())[0];
                // get new instance
            AbstractServiceExtension defaultExtension = (AbstractServiceExtension) context.getBean(beanName);
            extensions.add(defaultExtension);
        }
        return extensions;

//        if (aclProperties.isAdminFullAccess()){
//            AbstractServiceExtension<?, ? super ProxyController> adminExtension = (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("adminHasFullPermissionAboutSavedAclExtension");
//            defaultAclExtensions.add(adminExtension);
////            AbstractServiceExtension adminExtension = defaultAclExtensions.stream().filter(e -> e.getClass().equals(AdminHasFullPermissionAboutSavedAclExtension.class)).findFirst().get();
////            defaultAclExtensions.remove(adminExtension);
//        }
//        if (aclProperties.isCleanupAcl()){
//            AbstractServiceExtension<?, ? super ProxyController> cleanUpAclExtension = (AbstractServiceExtension<?, ? super ProxyController>) context.getBean("cleanUpAclExtension");
//            defaultAclExtensions.add(cleanUpAclExtension);
////            AbstractServiceExtension cleanupExtension = defaultAclExtensions.stream().filter(e -> e.getClass().equals(CleanUpAclExtension.class)).findFirst().get();
////            defaultAclExtensions.remove(cleanupExtension);
//        }
    }

}
