package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.service.plugin.BiDirChildPlugin;
import io.github.vincemann.generic.crud.lib.service.plugin.BiDirParentPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import(SessionReattachmentConfig.class)
@Profile("service")
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
