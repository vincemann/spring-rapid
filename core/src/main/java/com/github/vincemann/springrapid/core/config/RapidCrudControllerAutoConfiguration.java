package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.dto.DtoValidationStrategy;
import com.github.vincemann.springrapid.core.controller.dto.MergeUpdateStrategy;
import com.github.vincemann.springrapid.core.controller.dto.MergeUpdateStrategyImpl;
import com.github.vincemann.springrapid.core.controller.dto.map.JavaXDtoValidationStrategy;
import com.github.vincemann.springrapid.core.controller.id.IdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.id.LongUrlParamIdFetchingStrategy;
import com.github.vincemann.springrapid.core.controller.json.patch.ExtendedRemoveJsonPatchStrategy;
import com.github.vincemann.springrapid.core.controller.json.patch.JsonPatchStrategy;
import com.github.vincemann.springrapid.core.service.EndpointService;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SuppressWarnings("rawtypes")
@AutoConfigureAfter({RapidDtoMapperAutoConfiguration.class, RapidDtoLocatorAutoConfiguration.class})
@Configuration
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
    public EndpointService endpointService(RequestMappingHandlerMapping requestMappingHandlerMapping){
        return new EndpointService(requestMappingHandlerMapping);
    }

//    @ConditionalOnMissingBean(IdFetchingStrategy.class)
//    @Bean
//    public IdFetchingStrategy<Long> longIdFetchingStrategy(){
//        return new LongUrlParamIdFetchingStrategy();
//    }

    @ConditionalOnMissingBean(IdFetchingStrategy.class)
    @Bean
    public IdFetchingStrategy idFetchingStrategy(){
//        return new UrlParamIdFetchingStrategyImpl();
        return new LongUrlParamIdFetchingStrategy();
    }


    @ConditionalOnMissingBean(DtoValidationStrategy.class)
    @Bean
    public DtoValidationStrategy validationStrategy(javax.validation.Validator validator){
        //use spring validator, so dependency injection is supported
        return new JavaXDtoValidationStrategy(validator);
    }


}
