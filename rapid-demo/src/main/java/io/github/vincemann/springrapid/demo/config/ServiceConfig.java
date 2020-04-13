package io.github.vincemann.springrapid.demo.config;

import io.github.vincemann.springrapid.demo.service.OwnerService;
import io.github.vincemann.springrapid.demo.service.PetService;
import io.github.vincemann.springrapid.demo.service.plugin.AclPlugin;
import io.github.vincemann.springrapid.demo.service.plugin.OwnerOfTheYearPlugin;
import io.github.vincemann.springrapid.demo.service.plugin.SaveNameToWordPressDbPlugin;
import io.github.vincemann.springrapid.core.proxy.factory.CrudServicePluginProxyFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@io.github.vincemann.springrapid.core.slicing.config.ServiceConfig
public class ServiceConfig {

    @Primary
    @Bean
    public PetService extendedPetService(@Qualifier("noProxy") PetService petService,
                                         AclPlugin aclPlugin) {
        return CrudServicePluginProxyFactory.create(petService,
                aclPlugin
        );
    }

    @Primary
    @Bean
    public OwnerService ownerService(@Qualifier("noProxy") OwnerService ownerService,
                                     SaveNameToWordPressDbPlugin saveNameToWordPressDbPlugin,
                                     OwnerOfTheYearPlugin ownerOfTheYearPlugin,
                                     AclPlugin aclPlugin) {
        return CrudServicePluginProxyFactory.create(ownerService,
                saveNameToWordPressDbPlugin,
                ownerOfTheYearPlugin,
                aclPlugin
        );
    }
}
