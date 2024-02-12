package com.github.vincemann.springrapid.coredemo.config;

import com.github.vincemann.springrapid.core.proxy.CrudServiceExtensionProxyBuilder;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxyBuilder;
import com.github.vincemann.springrapid.coredemo.service.Root;
import org.springframework.context.annotation.Configuration;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.coredemo.service.ext.ExampleAclExtension;
import com.github.vincemann.springrapid.coredemo.service.ext.OwnerOfTheYearExtension;
import com.github.vincemann.springrapid.coredemo.service.ext.SaveNameToWordPressDbExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Add some Dummy Extensions to demonstrate proxy, extension system
 * Services are build onion style.
 * You can wrap each service/proxy with another proxy and each proxy can have extensions.
 * So you are creating diff versions of your service.
 *
 * Then you give every version a Qualifier annotation (i.E.: @Secured, @AclManaging), and can autowire
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
@Configuration
public class MyServiceConfig {

    @Primary
    @Bean
    public PetService petService(@Root PetService petService,
                                 ExampleAclExtension exampleAclExtension) {
        return new ExtensionProxyBuilder<>(petService)
                .addExtension(exampleAclExtension)
                .build();
    }

    @Primary
    @Bean
    public OwnerService ownerService(@Root OwnerService ownerService,
                                     OwnerOfTheYearExtension ownerOfTheYearExtension,
                                     ExampleAclExtension exampleAclExtension,
                                     SaveNameToWordPressDbExtension saveNameToWordPressDbExtension) {
        return new CrudServiceExtensionProxyBuilder<>(ownerService)
                .addGenericExtension(saveNameToWordPressDbExtension)
                .addGenericExtension(ownerOfTheYearExtension)
                .addExtension(exampleAclExtension)
                .build();
    }
}
