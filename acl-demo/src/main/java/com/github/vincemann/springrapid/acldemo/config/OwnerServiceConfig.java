package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.Root;
import com.github.vincemann.springrapid.acldemo.service.ext.acl.UserGainsAdminPermissionOnContainedCreatedUser;
import com.github.vincemann.springrapid.auth.service.ext.acl.UserGainsAdminPermissionOnCreated;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxies;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import static com.github.vincemann.springrapid.core.proxy.ExtensionProxies.crudProxy;

/**
 * Demonstrates programmatic style for adding extensions and creating multiple service versions/proxies.
 * For Annotation-based approach see i.E. {@link com.github.vincemann.springrapid.acldemo.service.jpa.JpaVetService}.
 */
@Configuration
public class OwnerServiceConfig {


    @Acl
    @Bean
    public OwnerService aclOwnerService(@Root OwnerService ownerService,
                                        UserGainsAdminPermissionOnContainedCreatedUser userGainsAdminPermissionOnContainedCreatedUser,
                                        UserGainsAdminPermissionOnCreated<Owner,Long> userGainsAdminPermissionOnCreated
    ) {
        return crudProxy(ownerService)
                .addGenericExtension(userGainsAdminPermissionOnContainedCreatedUser)
                .addGenericExtension(userGainsAdminPermissionOnCreated)
                .build();
    }

    @Secured
    @Bean
    public OwnerService securedOwnerService(@Acl OwnerService ownerService){
        // CrudAclChecksSecurityExtension will be added automatically by RapidDefaultSecurityExtensionAutoConfiguration
        return crudProxy(ownerService).build();
    }

}
