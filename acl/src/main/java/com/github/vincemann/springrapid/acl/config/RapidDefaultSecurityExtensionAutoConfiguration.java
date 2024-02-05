package com.github.vincemann.springrapid.acl.config;

import com.github.vincemann.springrapid.acl.AclProperties;
import com.github.vincemann.springrapid.acl.DefaultSecurityExtension;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.util.ProxyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.AopTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Adds default acl checks extension to all @{@link Secured} proxies.
 */
@Slf4j
@Configuration
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
    public void addDefaultSecurityExtension(AclProperties aclProperties, @Secured List<CrudService> securityProxies, @Autowired(required = false) @DefaultSecurityExtension Optional<List<ServiceExtension>> defaultSecurityExtensionsOptional){

        for (CrudService securityProxy : securityProxies) {
            ExtensionProxy proxy = ProxyUtils.getExtensionProxy(securityProxy);
            if (!proxy.getDefaultExtensionsEnabled()){
                log.debug("Default security extensions disabled for proxy: " + proxy);
                continue;
            }

            List<ServiceExtension> defaultSecurityExtensions = createDefaultSecurityExtensions(defaultSecurityExtensionsOptional);
            log.debug("Adding Default security extensions for proxy: " + proxy);
            for (ServiceExtension extension : defaultSecurityExtensions) {
                if (proxy.isIgnored((Class<? extends ServiceExtension>) AopTestUtils.getUltimateTargetObject(extension).getClass())){
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
    private List<ServiceExtension> createDefaultSecurityExtensions(Optional<List<ServiceExtension>> defaultSecurityExtensionsOptional) {
        List<ServiceExtension> defaultSecurityExtensions = new ArrayList<>(defaultSecurityExtensionsOptional.orElse(Collections.emptyList()));

        // stores new instances of extensions, that can be added by proxy
        List<ServiceExtension> extensions = new ArrayList<>();
        for (ServiceExtension extension : defaultSecurityExtensions) {
            String beanName = context.getBeanNamesForType(extension.getClass())[0];
            // get new instance
            ServiceExtension defaultExtension = (ServiceExtension) context.getBean(beanName);
            extensions.add(defaultExtension);
        }
        return extensions;
    }
}
