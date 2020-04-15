package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.bootstrap.DatabaseInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapAutoConfiguration {

    @Bean
    public DatabaseInitializer databaseInitializer(){
        return new DatabaseInitializer();
    }
}
