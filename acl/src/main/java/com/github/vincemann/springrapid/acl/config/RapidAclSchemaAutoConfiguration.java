package com.github.vincemann.springrapid.acl.config;

import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Creates Acl-Schema in db on startup.
 */
@Configuration
@Slf4j
public class RapidAclSchemaAutoConfiguration {

    public RapidAclSchemaAutoConfiguration() {

    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        log.debug("creating acl-schema via sql script");
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScript(new ClassPathResource("/acl-schema.sql"));
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }


}
