package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.boot.DatabaseDataInitManager;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Configuration
@Slf4j
public class RapidDatabaseInitAutoConfiguration {

    public RapidDatabaseInitAutoConfiguration() {

    }

    @Bean
    public DatabaseDataInitManager databaseInitializer(){
        return new DatabaseDataInitManager();
    }
}
