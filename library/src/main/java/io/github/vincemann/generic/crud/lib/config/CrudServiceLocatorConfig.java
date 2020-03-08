package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.service.locator.CrudServiceLocator;
import io.github.vincemann.generic.crud.lib.service.locator.PackageScanningCrudServiceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CrudServiceLocatorConfig {

    @Bean
    public CrudServiceLocator crudServiceLocator(){
        return new PackageScanningCrudServiceLocator();
    }
}
