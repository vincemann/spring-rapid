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


    @Autowired
    public void addDefaultSecurityExtension(@Secured List<CrudService> securityProxies, @Autowired(required = false) @DefaultSecurityExtension Optional<List<ServiceExtension>> defaultSecurityExtensionsOptional){

        for (CrudService securityProxy : securityProxies) {
            ExtensionProxy proxy = ProxyUtils.getExtensionProxy(securityProxy);
            if (!proxy.getDefaultExtensionsEnabled()){
                log.debug("default extensions disabled for proxy: " + proxy);
                continue;
            }

            List<ServiceExtension> defaultSecurityExtensions = createDefaultSecurityExtensions(defaultSecurityExtensionsOptional);
            for (ServiceExtension extension : defaultSecurityExtensions) {
                log.debug("checking if default security extensions for proxy should be added: " + extension);

                if (!extension.matchesProxy(proxy)){
                    log.debug("default security extension does not match proxy, skipping");
                    continue;
                }

                if (proxy.isIgnored((Class<? extends ServiceExtension>) AopTestUtils.getUltimateTargetObject(extension).getClass())){
                    log.info("ignoring default extension: " + extension.getClass().getSimpleName());
                    continue;
                }
                log.debug("adding default security extension to proxy");
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
