package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.bootstrap.DbDataInitManager;
import com.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@ServiceConfig
@Slf4j
public class RapidDatabaseInitAutoConfiguration {

    public RapidDatabaseInitAutoConfiguration() {
        log.info("Created");
    }

    @Bean
    public DbDataInitManager databaseInitializer(){
        return new DbDataInitManager();
    }
}
