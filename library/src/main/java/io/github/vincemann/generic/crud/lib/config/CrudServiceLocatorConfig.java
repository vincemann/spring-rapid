package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.config.layers.config.ServiceConfig;
import io.github.vincemann.generic.crud.lib.service.locator.CrudServiceLocator;
import io.github.vincemann.generic.crud.lib.service.locator.CrudServiceLocatorImpl;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@ServiceConfig
public class CrudServiceLocatorConfig {

    @ConditionalOnMissingBean
    @Bean
    public CrudServiceLocator crudServiceLocator(){
        return new CrudServiceLocatorImpl();
    }

    @Bean
    public BeanFactoryPostProcessor crudServiceLocatorBfpp(){
        return ((CrudServiceLocatorImpl) crudServiceLocator());
    }
}
