package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.CrudEndpointInfo;
import com.github.vincemann.springrapid.core.controller.ExtendedRemoveJsonPatchStrategy;
import com.github.vincemann.springrapid.core.controller.JsonPatchStrategy;
import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.LongUrlParamIdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.mergeUpdate.MergeUpdateStrategy;
import com.github.vincemann.springrapid.core.controller.mergeUpdate.MergeUpdateStrategyImpl;
import com.github.vincemann.springrapid.core.controller.parentAware.ParentAwareEndpointInfo;
import com.github.vincemann.springrapid.core.controller.validationStrategy.JavaXDtoValidationStrategy;
import com.github.vincemann.springrapid.core.controller.validationStrategy.DtoValidationStrategy;
import com.github.vincemann.springrapid.core.service.EndpointService;
import com.github.vincemann.springrapid.core.slicing.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@SuppressWarnings("rawtypes")
@AutoConfigureAfter({RapidDtoMapperAutoConfiguration.class, RapidDtoLocatorAutoConfiguration.class})
@WebConfig
@Slf4j
public class RapidCrudControllerAutoConfiguration {

    public RapidCrudControllerAutoConfiguration() {

    }

    @Bean
    @ConditionalOnMissingBean(MergeUpdateStrategy.class)
    public MergeUpdateStrategy mergeUpdateStrategy(){
        return new MergeUpdateStrategyImpl();
    }

    @Bean
    @ConditionalOnMissingBean(JsonPatchStrategy.class)
    public JsonPatchStrategy jsonPatchStrategy(){
        return new ExtendedRemoveJsonPatchStrategy();
    }


    @ConditionalOnMissingBean(EndpointService.class)
    @Bean
    public EndpointService endpointService(@Autowired RequestMappingHandlerMapping requestMappingHandlerMapping){
        return new EndpointService(requestMappingHandlerMapping);
    }

    @ConditionalOnMissingBean(IdFetchingStrategy.class)
    @Bean
    public IdFetchingStrategy<Long> longIdFetchingStrategy(){
        return new LongUrlParamIdFetchingStrategy();
    }

//    @ConditionalOnMissingBean(EndpointsExposureContext.class)
    @Bean
    @ConditionalOnMissingBean(name = "crudEndpointInfo")
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public CrudEndpointInfo crudEndpointInfo(){
        return new CrudEndpointInfo();
    }

    @Bean
    @ConditionalOnMissingBean(name = "parentAwareEndpointInfo")
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public ParentAwareEndpointInfo parentAwareEndpointInfo(){
        return new ParentAwareEndpointInfo();
    }

    @ConditionalOnMissingBean(DtoValidationStrategy.class)
    @Bean
    public DtoValidationStrategy validationStrategy(LocalValidatorFactoryBean localValidatorFactoryBean){
        //use spring validator, so dependency injection is supported
        return new JavaXDtoValidationStrategy(localValidatorFactoryBean.getValidator());
    }


}
