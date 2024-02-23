package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.controller.UrlParamWebExtensionParser;
import com.github.vincemann.springrapid.core.controller.WebExtensionParser;
import com.github.vincemann.springrapid.core.service.ctx.ContextService;
import com.github.vincemann.springrapid.core.service.ctx.CoreContextService;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.service.id.LongIdConverter;
import com.github.vincemann.springrapid.core.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableConfigurationProperties
// @Transactional annotations are placed with glibc proxy creation in mind
@EnableTransactionManagement(proxyTargetClass = true)
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class RapidGeneralAutoConfiguration {

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

    @ConditionalOnMissingBean(name = "idConverter")
    @Bean
    public IdConverter<Long> idConverter(){
        return new LongIdConverter();
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
    @ConditionalOnMissingBean(WebExtensionParser.class)
    public WebExtensionParser webExtensionParser(){
        return new UrlParamWebExtensionParser();
    }

    @Bean
    @ConditionalOnMissingBean(ContextService.class)
    public ContextService contextService(){
        return new CoreContextService();
    }

}
