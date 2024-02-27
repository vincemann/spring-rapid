package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.CoreProperties;
import com.github.vincemann.springrapid.core.controller.UrlParamWebExtensionParser;
import com.github.vincemann.springrapid.core.controller.WebExtensionParser;
import com.github.vincemann.springrapid.core.controller.dto.map.PrincipalFactory;
import com.github.vincemann.springrapid.core.controller.dto.map.PrincipalFactoryImpl;
import com.github.vincemann.springrapid.core.model.audit.LongIdSecurityAuditorAware;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContextImpl;
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
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableConfigurationProperties
@EnableTransactionManagement(proxyTargetClass = true) // @Transactional annotations are placed with glibc proxy creation in mind
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaAuditing
public class RapidCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Validator.class)
    public javax.validation.Validator localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

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

    @Bean
    @ConditionalOnMissingBean(RapidSecurityContext.class)
    public RapidSecurityContext rapidSecurityContext(){
        return new RapidSecurityContextImpl();
    }

    @Bean
    @ConditionalOnMissingBean(PrincipalFactory.class)
    public PrincipalFactory principalFactory(){
        return new PrincipalFactoryImpl();
    }

    @ConditionalOnMissingBean(name = "rapidSecurityAuditorAware")
    @Bean
    public AuditorAware<Long> auditorAware(){
        return new LongIdSecurityAuditorAware();
    }

}
