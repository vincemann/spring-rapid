package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.service.LemonService;
import com.github.vincemann.springlemon.auth.service.extension.LemonAclServiceExtension;
import com.github.vincemann.springlemon.auth.service.extension.LemonServiceSecurityExtension;
import com.github.vincemann.springrapid.acl.config.AclAutoConfiguration;
import com.github.vincemann.springrapid.acl.proxy.AclManaging;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acl.proxy.SecurityServiceExtensionProxyBuilderFactory;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.extensions.CleanUpAclServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.model.MutableAclService;


/**
 * Adds acl security stack in form of configuring @{@link AclManaging} and @{@link Secured} versions (proxies) of {@link LemonService}
 * with default Security- and AclExtensions.
 */
@ServiceConfig
@Slf4j
//we need the acl beans here
@AutoConfigureAfter({AclAutoConfiguration.class})
public class LemonServiceSecurityAutoConfiguration {

    public LemonServiceSecurityAutoConfiguration() {
        log.info("Created");
    }

    @Autowired SecurityServiceExtensionProxyBuilderFactory securityExtensionProxyBuilderFactory;
    @Autowired LocalPermissionService permissionService;
    @Autowired MutableAclService mutableAclService;


    @ConditionalOnMissingBean(LemonServiceSecurityExtension.class)
    @Bean
    public LemonServiceSecurityExtension lemonServiceSecurityRule() {
        return new LemonServiceSecurityExtension();
    }

    @Bean
    @ConditionalOnMissingBean(LemonAclServiceExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public LemonAclServiceExtension lemonAclExtension() {
        return new LemonAclServiceExtension();
    }


    @ConditionalOnMissingBean(name = "aclManagingLemonService")
    @Bean
    @AclManaging
    public LemonService<?, ?, ?> aclManagingLemonService(LemonService<?, ?, ?> service,
//                                                                            AdminFullAccessAclExtension adminFullAccess,
//                                                                            AuthenticatedFullAccessAclExtension authenticatedFullAccessAclExtension,
                                                         CleanUpAclServiceExtension cleanUpAclExtension) {
        return new ServiceExtensionProxyBuilder<>(service)
                .addExtensions(lemonAclExtension(), cleanUpAclExtension)
                .build();
    }


    @ConditionalOnMissingBean(name = "securedLemonService")
    @Bean
    @Secured
    public LemonService<?, ?, ?> securedLemonService(@AclManaging LemonService<?, ?, ?> service,
                                                     LemonServiceSecurityExtension securityRule) {
        return securityExtensionProxyBuilderFactory.create(service)
                .addExtensions(securityRule)
                .build();
    }

}
