package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.acl.proxy.Acl;
import com.github.vincemann.springrapid.acl.proxy.Secured;

import com.github.vincemann.springrapid.acl.service.extensions.acl.AuthenticatedHasFullPermissionAboutSavedAclExtension;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.Root;
import com.github.vincemann.springrapid.acldemo.service.extensions.AuthenticatedHasFullPermissionAboutSavedContainedUserAclExtension;
import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.context.annotation.Bean;

/**
 * Demonstrates First style for adding extensions and creation multiple service versions/proxies.
 * For Annotation-based approach see i.E. {@link com.github.vincemann.springrapid.acldemo.service.jpa.JpaVetService}.
 */
@ServiceConfig
public class OwnerServiceConfig {


    @Acl
    @Bean
    public OwnerService aclOwnerService(@Root OwnerService ownerService,
                                        AuthenticatedHasFullPermissionAboutSavedContainedUserAclExtension authenticatedFullAccessAboutSavedAclExtension,
                                        AuthenticatedHasFullPermissionAboutSavedAclExtension authenticatedHasFullPermissionAboutSavedAclExtension) {
        return new ServiceExtensionProxyBuilder<>(ownerService)
                .addGenericExtensions(authenticatedFullAccessAboutSavedAclExtension)
                .addExtensions(authenticatedHasFullPermissionAboutSavedAclExtension)
                .build();
    }

    @Secured
    @Bean
    public OwnerService securedOwnerService(@Acl OwnerService ownerService){
        // CrudAclChecksSecurityExtension will be added automatically by RapidSecuredProxyDefaultSecurityExtensionAutoConfiguration
        return new ServiceExtensionProxyBuilder<>(ownerService).build();
    }

}
