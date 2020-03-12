package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.config.layers.component.ServiceComponent;
import io.github.vincemann.generic.crud.lib.config.layers.config.ServiceConfig;
import io.github.vincemann.generic.crud.lib.service.plugin.BiDirChildPlugin;
import io.github.vincemann.generic.crud.lib.service.plugin.BiDirParentPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Import(SessionReattachmentConfig.class)
@ServiceConfig
public class CrudServicePluginConfig {

    @Bean
    public BiDirChildPlugin biDirChildPlugin(){
        return new BiDirChildPlugin();
    }

    @Bean
    public BiDirParentPlugin biDirParentPlugin(){
        return new BiDirParentPlugin();
    }
}
