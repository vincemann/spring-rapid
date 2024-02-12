package com.github.vincemann.springrapid.acl.config;


import com.github.vincemann.springrapid.acl.DefaultAclExtension;
import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.core.proxy.ServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxy;
import com.github.vincemann.springrapid.core.service.CrudService;
import org.springframework.context.annotation.Configuration;
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
@Configuration
public class RapidDefaultAclExtensionsAutoConfiguration {

    @Autowired
    ApplicationContext context;

    @Autowired
    public void addDefaultAclExtension(@Acl List<Object> aclProxies, @Autowired(required = false) @DefaultAclExtension Optional<List<ServiceExtension>> defaultAclExtensionsOptional) {

        for (Object aclProxy : aclProxies) {
            ExtensionProxy proxy = ProxyUtils.getExtensionProxy(aclProxy);
            if (!proxy.getDefaultExtensionsEnabled()){
                log.debug("default extensions disabled for proxy: " + proxy);
                continue;
            }

            List<ServiceExtension> defaultAclExtensions = createDefaultAclExtensions(defaultAclExtensionsOptional);

            for (ServiceExtension extension : defaultAclExtensions) {
                log.debug("checking if default acl extensions for proxy should be added: " + extension);
                if (!extension.matchesProxy(proxy)){
                    log.debug("default acl extension does not match proxy, skipping");
                    continue;
                }
                if (proxy.isIgnored((Class<? extends ServiceExtension>) AopTestUtils.getUltimateTargetObject(extension).getClass())){
                    log.info("ignoring default acl extension");
                    continue;
                }
                log.debug("adding default acl extension to proxy");
                proxy.addExtension(extension);
            }
        }
    }

    /**
     * scope of extensions has to be Prototype, because I need a new instance for each proxy of that extension.
     * Call this method for each proxy to get a new set of instances of the default extensions.
     */
    private List<ServiceExtension> createDefaultAclExtensions(Optional<List<ServiceExtension>> defaultAclExtensionsOptional){
        List<ServiceExtension> defaultAclExtensions = new ArrayList<>(defaultAclExtensionsOptional.orElse(Collections.emptyList()));

        // stores new instances of extensions, that can be added by proxy
        List<ServiceExtension> extensions = new ArrayList<>();
        for (ServiceExtension extension : defaultAclExtensions) {
            String beanName = context.getBeanNamesForType(extension.getClass())[0];
                // get new instance
            ServiceExtension defaultExtension = (ServiceExtension) context.getBean(beanName);
            extensions.add(defaultExtension);
        }
        return extensions;
    }

}
