package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.service.UserService;
import com.github.vincemann.springlemon.auth.service.extension.AclUserServiceExtension;
import com.github.vincemann.springlemon.auth.service.extension.UserServiceSecurityExtension;
import com.github.vincemann.springrapid.acl.config.RapidAclAutoConfiguration;
import com.github.vincemann.springrapid.acl.proxy.*;
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
 * Adds acl security stack in form of configuring @{@link AclManaging} and @{@link Secured} versions (proxies) of {@link UserService}
 * with default Security- and AclExtensions.
 */
@ServiceConfig
@Slf4j
//we need the acl beans here
@AutoConfigureAfter({RapidAclAutoConfiguration.class})
public class RapidUserServiceSecurityAutoConfiguration {

    
    @Autowired
    @DefaultSecurityServiceExtension
    SecurityServiceExtension<?> defaultSecurityServiceExtension;

    @Autowired
    LocalPermissionService permissionService;
    @Autowired
    MutableAclService mutableAclService;


    @ConditionalOnMissingBean(UserServiceSecurityExtension.class)
    @Bean
    public UserServiceSecurityExtension userServiceSecurityRule() {
        return new UserServiceSecurityExtension();
    }

    @Bean
    @ConditionalOnMissingBean(AclUserServiceExtension.class)
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AclUserServiceExtension lemonAclExtension() {
        return new AclUserServiceExtension();
    }


    @ConditionalOnMissingBean(name = "aclManagingUserService")
    @Bean
    @AclManaging
    public UserService<?, ?> aclManagingUserService(@Unsecured UserService<?, ?> service,
//                                                                            AdminFullAccessAclExtension adminFullAccess,
//                                                                            AuthenticatedFullAccessAclExtension authenticatedFullAccessAclExtension,
                                                        CleanUpAclServiceExtension cleanUpAclExtension) {
        return new ServiceExtensionProxyBuilder<>(service)
                .addExtensions(lemonAclExtension(), cleanUpAclExtension)
                .build();
    }


    @ConditionalOnMissingBean(name = "securedUserService")
    @Bean
    @Secured
    public UserService<?, ?> securedUserService(@AclManaging UserService<?, ?> service,
                                                    UserServiceSecurityExtension securityRule) {
        return new SecurityServiceExtensionProxyBuilder<>(service,defaultSecurityServiceExtension)
                .addExtensions(securityRule)
                .build();
    }

}
