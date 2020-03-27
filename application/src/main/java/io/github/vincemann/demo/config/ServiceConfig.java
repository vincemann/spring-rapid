package io.github.vincemann.demo.config;

import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.demo.service.plugin.AclPlugin;
import io.github.vincemann.demo.service.plugin.OwnerOfTheYearPlugin;
import io.github.vincemann.demo.service.plugin.SaveNameToWordPressDbPlugin;
import io.github.vincemann.generic.crud.lib.proxy.factory.CrudServicePluginProxyFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@io.github.vincemann.generic.crud.lib.config.layers.config.ServiceConfig
public class ServiceConfig {

    @Primary
    @Bean
    public PetService extendedPetService(@Qualifier("basic") PetService petService,
                                         //BiDirChildPlugin<Pet,Long> biDirChildPlugin,
                                         AclPlugin aclPlugin) {
        return CrudServicePluginProxyFactory.create(petService,
        //        biDirChildPlugin,
                aclPlugin
        );
    }

    //always use ownerService with core plugins when injecting OwnerService
    @Primary
    @Bean
    public OwnerService ownerService(@Qualifier("basic") OwnerService ownerService,
                                     //BiDirParentPlugin<Owner,Long> biDirParentPlugin,
                                     SaveNameToWordPressDbPlugin saveNameToWordPressDbPlugin,
                                     OwnerOfTheYearPlugin ownerOfTheYearPlugin,
                                     AclPlugin aclPlugin) {
        return CrudServicePluginProxyFactory.create(ownerService,
//                biDirParentPlugin,
                saveNameToWordPressDbPlugin,
                ownerOfTheYearPlugin,
                aclPlugin
        );
    }
}
