package io.github.vincemann.generic.crud.lib.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DelegatingFallbackToDefaultDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler.DtoCrudControllerExceptionHandler;
import io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler.DtoCrudControllerExceptionHandlerImpl;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.EndpointsExposureContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.LongUrlParamIdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SuppressWarnings("rawtypes")
@Configuration
@ConditionalOnProperty(name = "web.active",havingValue = "true",matchIfMissing = true)
public class SpringAdapterDtoCrudControllerConfig {

    @Bean
    public DtoCrudControllerExceptionHandler dtoCrudControllerExceptionHandlerBean() {
        return new DtoCrudControllerExceptionHandlerImpl();
    }

    @Bean
    public EndpointService endpointService(@Autowired RequestMappingHandlerMapping requestMappingHandlerMapping){
        return new EndpointService(requestMappingHandlerMapping);
    }

    @Qualifier("default")
    @Bean
    public DtoMapper defaultDtoMapper(){
        return new BasicDtoMapper();
    }

    @Primary
    @Bean
    public DtoMapper dtoMapper(@Autowired @Qualifier("default") DtoMapper defaultMapper){
        return new DelegatingFallbackToDefaultDtoMapper(defaultMapper);
    }


    @Value("${controller.idFetchingStrategy.idUrlParamKey}")
    private String idUrlParamKey;

    @Bean(name = "idUrlParamKey")
    public String getIdUrlParamKey(){
        return idUrlParamKey;
    }

    @Bean
    public IdFetchingStrategy<Long> longIdFetchingStrategy(){
        return new LongUrlParamIdFetchingStrategy(idUrlParamKey);
    }

    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EndpointsExposureContext endpointsExposureContextBean(){
        return new EndpointsExposureContext();
    }


    @Bean
    public ValidationStrategy validationStrategyBean(LocalValidatorFactoryBean localValidatorFactoryBean){
        //use spring validator, so dependency injection is supported
        return new JavaXValidationStrategy(localValidatorFactoryBean.getValidator());
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper= new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.getDeserializationConfig().with(MapperFeature.USE_STATIC_TYPING);
        return mapper;
    }



}
