package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.service.CrudServiceLocator;
import com.github.vincemann.springrapid.core.service.CrudServiceLocatorImpl;
import com.github.vincemann.springrapid.core.service.EntityLocator;
import com.github.vincemann.springrapid.core.service.EntityLocatorImpl;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.persistence.EntityManager;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
public class RapidServiceAutoConfiguration {

    @Autowired(required = false)
    EntityManager entityManager;



    @ConditionalOnMissingBean(CrudServiceLocator.class)
    @Bean
    public CrudServiceLocator crudServiceLocator(ConfigurableApplicationContext context, ConfigurableListableBeanFactory beanFactory){
        CrudServiceLocatorImpl locator = new CrudServiceLocatorImpl();
        context.addBeanFactoryPostProcessor(locator);
        locator.postProcessBeanFactory(beanFactory);
        return locator;
    }

    @Bean
    @ConditionalOnMissingBean(EntityLocator.class)
    public EntityLocator entityLocator(){
        return new EntityLocatorImpl();
    }

    @Autowired
    public void configureLazyToStringUtil(){
        LazyToStringUtil.setEntityManager(entityManager);
    }

}
