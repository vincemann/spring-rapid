package com.github.vincemann.springrapid.syncdemo.config;

import com.github.vincemann.springrapid.core.proxy.ExtensionProxies;
import com.github.vincemann.springrapid.core.proxy.ExtensionProxyBuilder;
import com.github.vincemann.springrapid.syncdemo.service.OwnerService;
import com.github.vincemann.springrapid.syncdemo.service.PetService;
import com.github.vincemann.springrapid.syncdemo.service.Root;
import com.github.vincemann.springrapid.syncdemo.service.ext.ExampleAclExtension;
import com.github.vincemann.springrapid.syncdemo.service.ext.OwnerOfTheYearExtension;
import com.github.vincemann.springrapid.syncdemo.service.ext.SaveNameToWordPressDbExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.github.vincemann.springrapid.core.proxy.ExtensionProxies.crudProxy;

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
@Configuration
public class MyServiceConfig {

    @Primary
    @Bean
    public PetService petService(@Root PetService petService, ExampleAclExtension aclServiceExtension) {
        return crudProxy(petService)
                .addExtension(aclServiceExtension)
                .build();
    }

    @Primary
    @Bean
    public OwnerService ownerService(@Root OwnerService ownerService,
                                     OwnerOfTheYearExtension ownerOfTheYearExtension,
                                     ExampleAclExtension aclServiceExtension,
                                     SaveNameToWordPressDbExtension saveNameToWordPressDbExtension
    ) {
        return crudProxy(ownerService)
                .addGenericExtension(saveNameToWordPressDbExtension)
                .addGenericExtension(ownerOfTheYearExtension)
                .addExtension(aclServiceExtension)
                .build();
    }
}
