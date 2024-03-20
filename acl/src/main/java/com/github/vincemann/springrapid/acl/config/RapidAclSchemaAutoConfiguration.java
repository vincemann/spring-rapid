package com.github.vincemann.springrapid.acl.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Creates Acl-Schema in db on startup.
 */
@Configuration
public class RapidAclSchemaAutoConfiguration {

    private final Log log = LogFactory.getLog(RapidAclSchemaAutoConfiguration.class);

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
