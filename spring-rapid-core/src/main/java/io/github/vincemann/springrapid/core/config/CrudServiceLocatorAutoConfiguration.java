package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocatorImpl;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@ServiceConfig
public class CrudServiceLocatorAutoConfiguration {

    @Primary
    @ConditionalOnMissingBean(CrudServiceLocator.class)
    @Bean
    public CrudServiceLocator crudServiceLocator(){
        return new CrudServiceLocatorImpl();
    }

    @Bean
    public BeanFactoryPostProcessor crudServiceLocatorBfpp(){
        return ((CrudServiceLocatorImpl) crudServiceLocator());
    }
}
