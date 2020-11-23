package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.extension.AclUserServiceExtension;
import com.github.vincemann.springrapid.auth.service.extension.UserServiceSecurityExtension;
import com.github.vincemann.springrapid.acl.config.RapidAclAutoConfiguration;
import com.github.vincemann.springrapid.acl.proxy.*;
import com.github.vincemann.springrapid.acl.service.LocalPermissionService;
import com.github.vincemann.springrapid.acl.service.extensions.CleanUpAclServiceExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
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
    LocalPermissionService permissionService;
    @Autowired
    MutableAclService mutableAclService;


    @ConditionalOnMissingBean(name = "userServiceSecurityExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public UserServiceSecurityExtension userServiceSecurityExtension() {
        return new UserServiceSecurityExtension();
    }

    @Bean
    @ConditionalOnMissingBean(name = "aclUserServiceExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AclUserServiceExtension aclUserServiceExtension() {
        return new AclUserServiceExtension();
    }


    @ConditionalOnMissingBean(name = "aclManagingUserService")
    @Bean
    @AclManaging
    public UserService<?, ?> aclManagingUserService(UserService<?, ?> service,
//                                                                            AdminFullAccessAclExtension adminFullAccess,
//                                                                            AuthenticatedFullAccessAclExtension authenticatedFullAccessAclExtension,
                                                    AclUserServiceExtension aclUserServiceExtension,
                                                    CleanUpAclServiceExtension cleanUpAclExtension
    ) {
        return new ServiceExtensionProxyBuilder<>(service)
                .addExtensions(aclUserServiceExtension, cleanUpAclExtension)
                .build();
    }


    @ConditionalOnMissingBean(name = "securedUserService")
    @Bean
    @Secured
    public UserService<?, ?> securedUserService(@AclManaging UserService<?, ?> service,
                                                UserServiceSecurityExtension securityRule
    ) {
        return new ServiceExtensionProxyBuilder<>(service)
                .addExtensions(securityRule)
                .build();
    }

}
