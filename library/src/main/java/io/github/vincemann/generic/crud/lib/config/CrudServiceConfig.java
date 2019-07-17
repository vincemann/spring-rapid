package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.service.crudServiceFinder.CrudServiceFinder;
import io.github.vincemann.generic.crud.lib.service.crudServiceFinder.PackageScanningCrudServiceFinder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrudServiceConfig {

    @Bean
    public CrudServiceFinder provideCrudServiceFinder(){
        return new PackageScanningCrudServiceFinder();
    }
}
