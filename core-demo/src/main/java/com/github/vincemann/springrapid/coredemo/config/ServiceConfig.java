package com.github.vincemann.springrapid.coredemo.config;

import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.coredemo.service.plugin.AclServiceExtension;
import com.github.vincemann.springrapid.coredemo.service.plugin.OwnerOfTheYearExtension;
import com.github.vincemann.springrapid.coredemo.service.plugin.SaveNameToWordPressDbExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@com.github.vincemann.springrapid.core.slicing.config.ServiceConfig
public class ServiceConfig {

    @Primary
    @Bean
    public PetService extendedPetService(@Qualifier("noProxy") PetService petService, AclServiceExtension aclServiceExtension) {
        return new ServiceExtensionProxyBuilder<>(petService)
                .addExtensions(aclServiceExtension)
                .build();
    }

    @Primary
    @Bean
    public OwnerService ownerService(@Qualifier("noProxy") OwnerService ownerService,
                                     OwnerOfTheYearExtension ownerOfTheYearExtension,
                                     AclServiceExtension aclServiceExtension,
                                     SaveNameToWordPressDbExtension saveNameToWordPressDbExtension) {
        return new ServiceExtensionProxyBuilder<>(ownerService)
                .addServiceExtensions(saveNameToWordPressDbExtension, ownerOfTheYearExtension)
                .addExtensions(aclServiceExtension)
                .build();
    }
}
