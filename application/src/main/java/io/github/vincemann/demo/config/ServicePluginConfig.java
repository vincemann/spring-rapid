package io.github.vincemann.demo.config;

import io.github.vincemann.demo.service.plugin.AclPlugin;
import io.github.vincemann.demo.service.plugin.OwnerOfTheYearPlugin;
import io.github.vincemann.demo.service.plugin.SaveNameToWordPressDbPlugin;
import io.github.vincemann.generic.crud.lib.config.CrudServicePluginConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import(CrudServicePluginConfig.class)
@Profile("service")
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
