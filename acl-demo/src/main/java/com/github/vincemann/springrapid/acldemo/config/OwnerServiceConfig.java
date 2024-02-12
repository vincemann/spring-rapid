package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.Root;
import com.github.vincemann.springrapid.acldemo.service.ext.acl.UserGainsAdminPermissionOnContainedUserOnCreate;
import com.github.vincemann.springrapid.auth.service.ext.acl.UserGainsAdminPermissionOnCreated;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxyBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

/**
 * Demonstrates First style for adding extensions and creating multiple service versions/proxies.
 * For Annotation-based approach see i.E. {@link com.github.vincemann.springrapid.acldemo.service.jpa.JpaVetService}.
 */
@Configuration
public class OwnerServiceConfig {


    @Acl
    @Bean
    public OwnerService aclOwnerService(@Root OwnerService ownerService,
                                        UserGainsAdminPermissionOnContainedUserOnCreate userGainsAdminPermissionOnContainedUserOnCreate,
                                        UserGainsAdminPermissionOnCreated<Owner,Long> userGainsAdminPermissionOnCreated
    ) {
        return new CrudServiceExtensionProxyBuilder<>(ownerService)
                .addGenericExtension(userGainsAdminPermissionOnContainedUserOnCreate)
                .addGenericExtension(userGainsAdminPermissionOnCreated)
                .build();
    }

    @Secured
    @Bean
    public OwnerService securedOwnerService(@Acl OwnerService ownerService){
        // CrudAclChecksSecurityExtension will be added automatically by RapidDefaultSecurityExtensionAutoConfiguration
        return new CrudServiceExtensionProxyBuilder<>(ownerService).build();
    }

}
