package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.model.RapidSecurityAuditorAware;
import com.github.vincemann.springrapid.core.util.EntityLocator;
import com.github.vincemann.springrapid.core.util.JpaUtils;
import com.github.vincemann.springrapid.core.util.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
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


    // overwrite existing autoraware
    @ConditionalOnMissingBean(name = "rapidSecurityAuditorAware")
    @Bean
    public AuditorAware auditorAware(){
        return new RapidSecurityAuditorAware();
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
    public EntityLocator entityLocator(){
        return new EntityLocator();
    }

//    @Bean
//    @ConditionalOnMissingBean(LazyLogUtils.class)
//    public LazyLogUtils lazyInitLogUtils(){
//        return LazyLogUtils.create(entityManager);
//    }

}
