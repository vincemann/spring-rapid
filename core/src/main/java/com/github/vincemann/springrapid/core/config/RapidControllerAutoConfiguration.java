package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.DtoClassLocator;
import com.github.vincemann.springrapid.core.controller.DelegatingDtoClassLocator;
import com.github.vincemann.springrapid.core.controller.RapidDtoClassLocator;
import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.controller.EndpointsExposureContext;
import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.idFetchingStrategy.LongUrlParamIdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.mergeUpdate.MergeUpdateStrategy;
import com.github.vincemann.springrapid.core.controller.mergeUpdate.MergeUpdateStrategyImpl;
import com.github.vincemann.springrapid.core.controller.validationStrategy.JavaXValidationStrategy;
import com.github.vincemann.springrapid.core.controller.validationStrategy.ValidationStrategy;
import com.github.vincemann.springrapid.core.service.EndpointService;
import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@SuppressWarnings("rawtypes")
@AutoConfigureAfter(DtoMapperAutoConfiguration.class)
@WebConfig
@Slf4j
public class RapidControllerAutoConfiguration {

    public RapidControllerAutoConfiguration() {
        log.info("Created");
    }

    @Value("${controller.idFetchingStrategy.idUrlParamKey:id}")
    private String idUrlParamKey;

    @Bean(name = "idUrlParamKey")
    public String idUrlParamKey(){
        return idUrlParamKey;
    }


    @Bean
    @ConditionalOnMissingBean(MergeUpdateStrategy.class)
    public MergeUpdateStrategy mergeUpdateStrategy(){
        return new MergeUpdateStrategyImpl();
    }

    @Bean
    @ConditionalOnMissingBean(name = "delegatingOwnerLocator")
    public DelegatingOwnerLocator delegatingOwnerLocator(List<OwnerLocator> locators){
        DelegatingOwnerLocator delegatingLocator = new DelegatingOwnerLocator();
        if (locators.isEmpty()){
            log.warn("No OwnerLocatorBean found -> dtoMapping principal feature will be ignored.");
        }
        locators.forEach(delegatingLocator::register);
        return delegatingLocator;
    }

    @Bean
    @ConditionalOnMissingBean(DtoClassLocator.class)
    public DtoClassLocator dtoClassLocator(){
        return new RapidDtoClassLocator();
    }


    @Bean
    @ConditionalOnMissingBean(name = "extendableDtoClassLocator")
    @Scope(SCOPE_PROTOTYPE)
    public DelegatingDtoClassLocator extendableDtoClassLocator(DtoClassLocator globalLocator){
        return new DelegatingDtoClassLocator(globalLocator);
    }


    @ConditionalOnMissingBean(EndpointService.class)
    @Bean
    public EndpointService endpointService(@Autowired RequestMappingHandlerMapping requestMappingHandlerMapping){
        return new EndpointService(requestMappingHandlerMapping);
    }

    @ConditionalOnMissingBean(IdFetchingStrategy.class)
    @Bean
    public IdFetchingStrategy<Long> longIdFetchingStrategy(){
        return new LongUrlParamIdFetchingStrategy(idUrlParamKey());
    }

//    @ConditionalOnMissingBean(EndpointsExposureContext.class)
    @Bean
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public EndpointsExposureContext endpointsExposureContext(){
        return new EndpointsExposureContext();
    }

    @ConditionalOnMissingBean(ValidationStrategy.class)
    @Bean
    public ValidationStrategy validationStrategy(LocalValidatorFactoryBean localValidatorFactoryBean){
        //use spring validator, so dependency injection is supported
        return new JavaXValidationStrategy(localValidatorFactoryBean.getValidator());
    }


}
