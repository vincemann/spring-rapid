package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.Root;
import com.github.vincemann.springrapid.acldemo.service.ext.UserGainsAdminPermissionAboutSavedContainedUserAclExtension;
import com.github.vincemann.springrapid.auth.service.extension.UserGainsAdminPermissionAboutSelfAclExtension;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.context.annotation.Bean;

/**
 * Demonstrates First style for adding extensions and creating multiple service versions/proxies.
 * For Annotation-based approach see i.E. {@link com.github.vincemann.springrapid.acldemo.service.jpa.JpaVetService}.
 */
@ServiceConfig
public class OwnerServiceConfig {


    @Acl
    @Bean
    public OwnerService aclOwnerService(@Root OwnerService ownerService,
                                        UserGainsAdminPermissionAboutSavedContainedUserAclExtension userHasFullPermissionAboutSavedContainedUserAclExtension,
                                        UserGainsAdminPermissionAboutSelfAclExtension<Owner,Long> userGainsAdminPermissionAboutSelfAclExtension
    ) {
        return new ExtensionProxyBuilder<>(ownerService)
                .addGenericExtension(userHasFullPermissionAboutSavedContainedUserAclExtension)
                .addGenericExtension(userGainsAdminPermissionAboutSelfAclExtension)
                .build();
    }

    @Secured
    @Bean
    public OwnerService securedOwnerService(@Acl OwnerService ownerService){
        // CrudAclChecksSecurityExtension will be added automatically by RapidDefaultSecurityExtensionAutoConfiguration
        return new ExtensionProxyBuilder<>(ownerService).build();
    }

}
