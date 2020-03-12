package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.config.layers.component.WebComponent;
import io.github.vincemann.generic.crud.lib.config.layers.config.WebConfig;
import io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler.DtoCrudControllerExceptionHandler;
import io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler.DtoCrudControllerExceptionHandlerImpl;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.EndpointsExposureContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.LongUrlParamIdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
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
    public DtoCrudControllerExceptionHandler dtoCrudControllerExceptionHandler() {
        return new DtoCrudControllerExceptionHandlerImpl();
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
