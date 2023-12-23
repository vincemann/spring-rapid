package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.IdConverter;
import com.github.vincemann.springrapid.core.LongIdConverter;
import com.github.vincemann.springrapid.core.model.LongIdRapidSecurityAuditorAware;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContext;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextFactory;
import com.github.vincemann.springrapid.core.service.context.ServiceCallContextHolder;
import com.github.vincemann.springrapid.core.service.locator.CrudServiceLocator;
import com.github.vincemann.springrapid.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.persistence.EntityManager;

@Configuration
@EnableConfigurationProperties
@Slf4j
public class RapidGeneralAutoConfiguration {

    @Autowired(required = false)
    EntityManager entityManager;


    public RapidGeneralAutoConfiguration() {

    }

    @Autowired
    @ConditionalOtherConfig("com.github.vincemann.springrapid.auth.config.RapidAuthGeneralAutoConfiguration")
    public void configureServiceCallContext(){
        ServiceCallContextHolder.initialize(new ServiceCallContextFactory() {
            @Override
            public ServiceCallContext create() {
                return new ServiceCallContext();
            }
        });
    }

    @Bean
    @ConditionalOnMissingBean(Validator.class)
    public javax.validation.Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    // is already autodefined by spring, dont override by redefining
//    @Bean
//    public MessageSource messageSource() {
//        ReloadableResourceBundleMessageSource messageSource
//                = new ReloadableResourceBundleMessageSource();
//
//        messageSource.setBasename("classpath:messages");
//        messageSource.setDefaultEncoding("UTF-8");
//        return messageSource;
//    }

    @ConditionalOnMissingBean(name = "idConverter")
    @Bean
    public IdConverter<Long> idConverter(){
        return new LongIdConverter();
    }

    // you can overwrite existing autorAware, is done in auth modules autoconfig
    // if you need an other type than long, define a bean with name rapidSecurityAuditorAware with other generic parameter
    @ConditionalOnMissingBean(name = "rapidSecurityAuditorAware")
    @Bean
    public AuditorAware<Long> auditorAware(){
        return new LongIdRapidSecurityAuditorAware();
    }

    @Bean
    public Message messageUtils(MessageSource messageSource){
        return new Message(messageSource);
    }

    @Bean
    @ConditionalOnMissingBean(CoreProperties.class)
    @ConfigurationProperties(prefix = "rapid-core")
    public CoreProperties coreProperties(){
        return new CoreProperties();
    }

    @Bean
    @ConditionalOnMissingBean(JpaUtils.class)
    public JpaUtils jpaUtils(){
        return new JpaUtils(entityManager);
    }

    @Bean
    @ConditionalOnMissingBean(EntityLocator.class)
    public EntityLocator entityLocator(CrudServiceLocator crudServiceLocator){
        return new EntityLocator(crudServiceLocator);
    }

//    @Bean
//    @ConditionalOnMissingBean(LazyLogUtils.class)
//    public LazyLogUtils lazyInitLogUtils(){
//        return LazyLogUtils.create(entityManager);
//    }

}
