package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.acl.config.RapidAclExtensionsAutoConfiguration;
import com.github.vincemann.springrapid.acl.service.AclPermissionService;
import com.github.vincemann.springrapid.acl.service.extensions.security.CrudAclChecksSecurityExtension;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.extension.AclUserExtension;
import com.github.vincemann.springrapid.auth.service.extension.UserServiceSecurityExtension;
import com.github.vincemann.springrapid.acl.config.RapidAclAutoConfiguration;
import com.github.vincemann.springrapid.acl.proxy.*;
import com.github.vincemann.springrapid.acl.service.RapidPermissionService;
import com.github.vincemann.springrapid.acl.service.extensions.acl.CleanUpAclExtension;
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
 * Adds acl security stack in form of configuring @{@link Acl} and @{@link Secured} versions (proxies) of {@link UserService}
 * with default Security- and AclExtensions.
 */
@ServiceConfig
@Slf4j
//we need the acl beans here
@AutoConfigureAfter({RapidAclAutoConfiguration.class, RapidAclExtensionsAutoConfiguration.class})
public class RapidUserServiceSecurityAutoConfiguration {


    @Autowired
    AclPermissionService permissionService;

    @Autowired
    MutableAclService mutableAclService;


    @ConditionalOnMissingBean(name = "userServiceSecurityExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public UserServiceSecurityExtension userServiceSecurityExtension() {
        return new UserServiceSecurityExtension();
    }

    @Bean("aclUserServiceExtension")
    @ConditionalOnMissingBean(name = "aclUserServiceExtension")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AclUserExtension aclUserServiceExtension() {
        return new AclUserExtension();
    }


    @ConditionalOnMissingBean(name = "aclUserService")
    @Bean
    @Acl
    public UserService<?, ?> aclUserService(UserService<?, ?> service,
                                            // Extensions are added by AutoConfig
////                                                                            AdminFullAccessAclExtension adminFullAccess,
////                                                                            AuthenticatedFullAccessAclExtension authenticatedFullAccessAclExtension,
                                            CleanUpAclExtension cleanUpAclExtension,
                                            AclUserExtension aclUserServiceExtension
    ) {
        return new ServiceExtensionProxyBuilder<>(service)
                .toggleDefaultExtensions(false)
                .addExtension(aclUserServiceExtension)
                .addExtension(cleanUpAclExtension)
                .build();
    }


    @ConditionalOnMissingBean(name = "securedUserService")
    @Bean
    @Secured
    public UserService<?, ?> securedUserService(@Acl UserService<?, ?> service,
                                                UserServiceSecurityExtension securityRule,
                                                CrudAclChecksSecurityExtension crudAclChecksSecurityExtension
    ) {
        return new ServiceExtensionProxyBuilder<>(service)
                .toggleDefaultExtensions(false)
                .addExtension(securityRule)
                .addExtension(crudAclChecksSecurityExtension)
                .build();
    }

}
