package com.github.vincemann.springrapid.acldemo.controller.config;

import com.github.vincemann.springrapid.auth.bootstrap.AdminInitializer;
import com.github.vincemann.springrapid.core.bootstrap.DatabaseInitializer;
import com.github.vincemann.springrapid.coretest.boot.BeforeEachTestInitializable;
import com.github.vincemann.springrapid.coretest.slicing.TestConfig;
import org.springframework.context.annotation.Bean;

@TestConfig
public class AdminTestConfig {

    @Bean
    @BeforeEachTestInitializable
    public DatabaseInitializer adminDatabaseDataInitializer(AdminInitializer adminInitializer){
        return adminInitializer;
    }
}
