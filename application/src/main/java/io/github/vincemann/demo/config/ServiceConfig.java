package io.github.vincemann.demo.config;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.demo.service.plugin.AclPlugin;
import io.github.vincemann.demo.service.plugin.OwnerOfTheYearPlugin;
import io.github.vincemann.demo.service.plugin.SaveNameToWordPressDbPlugin;
import io.github.vincemann.generic.crud.lib.proxy.factory.CrudServicePluginProxyFactory;
import io.github.vincemann.generic.crud.lib.service.plugin.BiDirChildPlugin;
import io.github.vincemann.generic.crud.lib.service.plugin.BiDirParentPlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

@Import(ServicePluginConfig.class)
@io.github.vincemann.generic.crud.lib.config.layers.config.ServiceConfig
public class ServiceConfig {

    @Primary
    @Bean
    public PetService extendedPetService(@Qualifier("basic") PetService petService,
                                         BiDirChildPlugin<Pet,Long> biDirChildPlugin,
                                         AclPlugin aclPlugin) {
        return CrudServicePluginProxyFactory.create(petService,
                biDirChildPlugin,
                aclPlugin
        );
    }

    //always use ownerService with core plugins when injecting OwnerService
    @Primary
    @Bean
    public OwnerService ownerService(@Qualifier("basic") OwnerService ownerService,
                                     BiDirParentPlugin<Owner,Long> biDirParentPlugin,
                                     SaveNameToWordPressDbPlugin saveNameToWordPressDbPlugin,
                                     //SessionReattachmentPlugin sessionReattachmentPlugin,
                                     OwnerOfTheYearPlugin ownerOfTheYearPlugin,
                                     AclPlugin aclPlugin) {
        return CrudServicePluginProxyFactory.create(ownerService,
                //sessionReattachmentPlugin,
                biDirParentPlugin,
                saveNameToWordPressDbPlugin,
                ownerOfTheYearPlugin,
                aclPlugin
        );
    }
}
