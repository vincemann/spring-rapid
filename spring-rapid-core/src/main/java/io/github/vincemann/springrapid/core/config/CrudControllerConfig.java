package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.config.layers.config.WebConfig;
import io.github.vincemann.springrapid.core.controller.springAdapter.EndpointsExposureContext;
import io.github.vincemann.springrapid.core.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.springAdapter.idFetchingStrategy.LongUrlParamIdFetchingStrategy;
import io.github.vincemann.springrapid.core.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.springrapid.core.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.springrapid.core.service.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SuppressWarnings("rawtypes")
@Import(DtoMapperConfig.class)
@WebConfig
public class CrudControllerConfig {

    @Value("${controller.idFetchingStrategy.idUrlParamKey}")
    private String idUrlParamKey;

    @Bean(name = "idUrlParamKey")
    public String idUrlParamKey(){
        return idUrlParamKey;
    }


    @ConditionalOnMissingBean
    @Bean
    public EndpointService endpointService(@Autowired RequestMappingHandlerMapping requestMappingHandlerMapping){
        return new EndpointService(requestMappingHandlerMapping);
    }

    @ConditionalOnMissingBean
    @Bean
    public IdFetchingStrategy<Long> longIdFetchingStrategy(){
        return new LongUrlParamIdFetchingStrategy(idUrlParamKey());
    }

    @ConditionalOnMissingBean
    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EndpointsExposureContext endpointsExposureContext(){
        return new EndpointsExposureContext();
    }

    @ConditionalOnMissingBean
    @Bean
    public ValidationStrategy validationStrategy(LocalValidatorFactoryBean localValidatorFactoryBean){
        //use spring validator, so dependency injection is supported
        return new JavaXValidationStrategy(localValidatorFactoryBean.getValidator());
    }


}
