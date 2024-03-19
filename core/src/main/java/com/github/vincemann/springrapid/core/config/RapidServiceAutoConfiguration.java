package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.service.RepositoryLocator;
import com.github.vincemann.springrapid.core.service.RepositoryAccessor;
import com.github.vincemann.springrapid.core.service.EntityLocator;
import com.github.vincemann.springrapid.core.service.EntityLocatorImpl;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class RapidServiceAutoConfiguration {

    @Autowired(required = false)
    EntityManager entityManager;

    @Bean
    @ConditionalOnMissingBean(RepositoryLocator.class)
    public RepositoryLocator crudServiceLocator(){
        return new RepositoryAccessor();
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
