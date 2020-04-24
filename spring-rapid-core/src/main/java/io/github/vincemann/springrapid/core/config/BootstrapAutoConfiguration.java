package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.bootstrap.DatabaseInitializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class BootstrapAutoConfiguration {

    public BootstrapAutoConfiguration() {
        log.info("Created");
    }

    @Bean
    public DatabaseInitializer databaseInitializer(){
        return new DatabaseInitializer();
    }
}
