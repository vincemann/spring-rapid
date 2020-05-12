package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.slicing.config.ServiceConfig;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import io.github.vincemann.springrapid.core.service.locator.CrudServiceLocatorImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@ServiceConfig
@Slf4j
public class CrudServiceLocatorAutoConfiguration {

    public CrudServiceLocatorAutoConfiguration() {
        log.info("Created");
    }

    @ConditionalOnMissingBean(CrudServiceLocator.class)
    @Bean
    public CrudServiceLocator crudServiceLocator(ConfigurableApplicationContext context, ConfigurableListableBeanFactory beanFactory){
        CrudServiceLocatorImpl csl = new CrudServiceLocatorImpl();
        context.addBeanFactoryPostProcessor(csl);
        csl.postProcessBeanFactory(beanFactory);
        return csl;
    }

}
