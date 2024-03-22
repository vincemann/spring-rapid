package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.service.EndpointService;
import com.github.vincemann.springrapid.core.service.RepositoryAccessor;
import com.github.vincemann.springrapid.core.service.id.IdConverter;
import com.github.vincemann.springrapid.core.service.id.LongIdConverter;
import com.github.vincemann.springrapid.core.util.LazyToStringUtil;
import com.github.vincemann.springrapid.core.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.persistence.EntityManager;

@Configuration
@EnableConfigurationProperties
@EnableTransactionManagement(proxyTargetClass = true) // @Transactional annotations are placed with glibc proxy creation in mind
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaAuditing
public class RapidCoreAutoConfiguration {


    @Autowired(required = false)
    EntityManager entityManager;

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

    @ConditionalOnMissingBean(EndpointService.class)
    @Bean
    public EndpointService endpointService(RequestMappingHandlerMapping requestMappingHandlerMapping){
        return new EndpointService(requestMappingHandlerMapping);
    }

    @Autowired
    public void configureLazyToStringUtil(){
        LazyToStringUtil.setEntityManager(entityManager);
    }

    @Bean
    @ConditionalOnMissingBean(name = "repositoryAccessor")
    public RepositoryAccessor repositoryAccessor(){
        return new RepositoryAccessor();
    }

}
