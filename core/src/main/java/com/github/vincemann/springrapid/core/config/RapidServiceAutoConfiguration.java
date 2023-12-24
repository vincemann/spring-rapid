package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocatorImpl;
import com.github.vincemann.springrapid.core.slicing.ServiceConfig;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@ServiceConfig
public class RapidServiceAutoConfiguration {

    @ConditionalOnMissingBean(CrudServiceLocator.class)
    @Bean
    public CrudServiceLocator crudServiceLocator(ConfigurableApplicationContext context, ConfigurableListableBeanFactory beanFactory){
        CrudServiceLocatorImpl csl = new CrudServiceLocatorImpl();
        context.addBeanFactoryPostProcessor(csl);
        csl.postProcessBeanFactory(beanFactory);
        return csl;
    }


}
