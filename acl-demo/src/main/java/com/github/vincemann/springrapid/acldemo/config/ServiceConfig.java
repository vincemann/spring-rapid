package com.github.vincemann.springrapid.acldemo.config;

import com.github.vincemann.springrapid.core.proxy.ServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.acldemo.service.Root;
import com.github.vincemann.springrapid.acldemo.service.extensions.AclServiceExtension;
import com.github.vincemann.springrapid.acldemo.service.extensions.OwnerOfTheYearExtension;
import com.github.vincemann.springrapid.acldemo.service.extensions.SaveNameToWordPressDbExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Add some Dummy Extensions to demonstrate proxy, extension system
 * Services are build onion style.
 * You can wrap each service/proxy with another proxy and each proxy can have extensions.
 * So you are creating diff versions of your service.
 *
 * Then you give every version a Qualifier annotation (i.E.: @Secured, @AclManaging, @Root), and can autowire
 * the specific version you need in. i.E.:
 *
 * @Autowired
 * @Secured
 * private IService securedService;
 *
 *
 * @Autowired
 * @Root
 * private IService service;
 */
@com.github.vincemann.springrapid.core.slicing.ServiceConfig
public class ServiceConfig {

    @Primary
    @Bean
    public PetService extendedPetService(@Root PetService petService, AclServiceExtension aclServiceExtension) {
        return new ServiceExtensionProxyBuilder<>(petService)
                .addExtensions(aclServiceExtension)
                .build();
    }

    @Primary
    @Bean
    public OwnerService ownerService(@Root OwnerService ownerService,
                                     OwnerOfTheYearExtension ownerOfTheYearExtension,
                                     AclServiceExtension aclServiceExtension,
                                     SaveNameToWordPressDbExtension saveNameToWordPressDbExtension) {
        return new ServiceExtensionProxyBuilder<>(ownerService)
                .addServiceExtensions(saveNameToWordPressDbExtension, ownerOfTheYearExtension)
                .addExtensions(aclServiceExtension)
                .build();
    }
}
