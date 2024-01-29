package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.acl.config.RapidAclExtensionsAutoConfiguration;
import com.github.vincemann.springrapid.acl.service.AclPermissionService;
import com.github.vincemann.springrapid.acl.service.ext.sec.CrudAclChecksSecurityExtension;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.auth.service.extension.AclUserExtension;
import com.github.vincemann.springrapid.auth.service.extension.UserServiceSecurityExtension;
import com.github.vincemann.springrapid.acl.config.RapidAclAutoConfiguration;
import com.github.vincemann.springrapid.acl.proxy.*;
import com.github.vincemann.springrapid.acl.service.ext.acl.CleanUpAclExtension;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxyBuilder;
import org.springframework.context.annotation.Configuration;
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
@Configuration
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
                                            CleanUpAclExtension cleanUpAclExtension,
                                            // all other relevant acl stuff in here:
                                            AclUserExtension aclUserServiceExtension
    ) {
        return new ExtensionProxyBuilder<>(service)
                // dont work with default extensions to keep things simple and concrete for user
                .setDefaultExtensionsEnabled(false)
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
        return new ExtensionProxyBuilder<>(service)
                // dont work with default extensions to keep things safer for user related stuff
                .setDefaultExtensionsEnabled(false)
                .addExtension(securityRule)
                .addExtension(crudAclChecksSecurityExtension)
                .build();
    }

}
