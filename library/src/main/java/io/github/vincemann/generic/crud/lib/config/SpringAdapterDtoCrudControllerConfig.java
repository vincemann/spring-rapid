package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler.DtoCrudControllerExceptionHandler;
import io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler.DtoCrudControllerExceptionHandlerImpl;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.LongUrlParamIdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.JSONMediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.JavaXValidationStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SuppressWarnings("rawtypes")
@Configuration
public class SpringAdapterDtoCrudControllerConfig {

    @Bean
    public DtoCrudControllerExceptionHandler dtoCrudControllerExceptionHandler() {
        return new DtoCrudControllerExceptionHandlerImpl();
    }


    @Qualifier("default")
    @Bean
    public DtoMapper defaultDtoMapper(){
        return new BasicDtoMapper();
    }

    @Value("${controller.idFetchingStrategy.idUrlParamKey}")
    private String idUrlParamKey;

    @Bean(name = "idUrlParamKey")
    public String getIdUrlParamKey(){
        return idUrlParamKey;
    }

    @Bean
    public IdFetchingStrategy<Long> getLongIdFetchingStrategy(){
        return new LongUrlParamIdFetchingStrategy(idUrlParamKey);
    }

    @Bean
    public MediaTypeStrategy getMediaTypeStrategy(){
        return new JSONMediaTypeStrategy();
    }

    @Bean
    public ValidationStrategy getValidationStrategy(LocalValidatorFactoryBean localValidatorFactoryBean){
        //use spring validator, so dependency injection is supported
        return new JavaXValidationStrategy(localValidatorFactoryBean.getValidator());
    }

}
