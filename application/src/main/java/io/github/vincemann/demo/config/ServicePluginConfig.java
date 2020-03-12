package io.github.vincemann.demo.config;

import io.github.vincemann.demo.service.plugin.AclPlugin;
import io.github.vincemann.demo.service.plugin.OwnerOfTheYearPlugin;
import io.github.vincemann.demo.service.plugin.SaveNameToWordPressDbPlugin;
import io.github.vincemann.generic.crud.lib.config.CrudServicePluginConfig;
import io.github.vincemann.generic.crud.lib.config.layers.config.ServiceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;


@Import(CrudServicePluginConfig.class)
@ServiceConfig
public class ServicePluginConfig {

    @Bean
    public AclPlugin aclPlugin(){
        return new AclPlugin();
    }

    @Bean
    public OwnerOfTheYearPlugin ownerOfTheYearPlugin(){
        return new OwnerOfTheYearPlugin();
    }

    @Bean
    public SaveNameToWordPressDbPlugin saveNameToWordPressDbPlugin(){
        return new SaveNameToWordPressDbPlugin();
    }


}
