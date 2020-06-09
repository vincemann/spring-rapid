package com.github.vincemann.springrapid.demo.config;

import com.github.vincemann.springrapid.demo.service.OwnerService;
import com.github.vincemann.springrapid.demo.service.PetService;
import com.github.vincemann.springrapid.demo.service.plugin.AclPlugin;
import com.github.vincemann.springrapid.demo.service.plugin.OwnerOfTheYearPlugin;
import com.github.vincemann.springrapid.demo.service.plugin.SaveNameToWordPressDbPlugin;
import com.github.vincemann.springrapid.core.proxy.factory.CrudServicePluginProxyFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@com.github.vincemann.springrapid.core.slicing.config.ServiceConfig
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
