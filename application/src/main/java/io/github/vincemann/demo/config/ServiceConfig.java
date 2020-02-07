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
import io.github.vincemann.generic.crud.lib.service.plugin.SessionReattachmentPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ServiceConfig {

    private CrudServicePluginProxyFactory pluginProxyFactory;

    @Autowired
    public ServiceConfig(CrudServicePluginProxyFactory pluginProxyFactory) {
        this.pluginProxyFactory = pluginProxyFactory;
    }

    //always use ownerService with core plugins when injecting OwnerService
    @Primary
    @Bean
    public OwnerService ownerService(@Qualifier("basic") OwnerService ownerService,
                                     BiDirParentPlugin<Owner,Long> biDirParentPlugin,
                                     SaveNameToWordPressDbPlugin saveNameToWordPressDbPlugin,
                                     SessionReattachmentPlugin sessionReattachmentPlugin,
                                     OwnerOfTheYearPlugin ownerOfTheYearPlugin,
                                     AclPlugin aclPlugin) {
        return pluginProxyFactory.create(ownerService,
                sessionReattachmentPlugin,
                biDirParentPlugin,
                saveNameToWordPressDbPlugin,
                ownerOfTheYearPlugin,
                aclPlugin
        );
    }

    @Primary
    @Bean
    public PetService extendedPetService(@Qualifier("basic") PetService petService,
                                         BiDirChildPlugin<Pet,Long> biDirChildPlugin,
                                         AclPlugin aclPlugin) {
        return pluginProxyFactory.create(petService,
                biDirChildPlugin,
                aclPlugin
        );
    }

}
