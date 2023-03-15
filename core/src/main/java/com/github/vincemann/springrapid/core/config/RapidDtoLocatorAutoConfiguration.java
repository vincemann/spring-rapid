package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.DelegatingDtoClassLocator;
import com.github.vincemann.springrapid.core.controller.DtoClassLocator;
import com.github.vincemann.springrapid.core.controller.RoleFallbackDtoClassLocator;
import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import com.github.vincemann.springrapid.core.slicing.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.List;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@WebConfig
@Slf4j
public class RapidDtoLocatorAutoConfiguration {

    // FIND RIGHT DTO CLASS
    @Bean
    @ConditionalOnMissingBean(name = "delegatingOwnerLocator")
    public DelegatingOwnerLocator delegatingOwnerLocator(List<OwnerLocator> locators){
        DelegatingOwnerLocator delegatingLocator = new DelegatingOwnerLocator();
        if (locators.isEmpty()){
            if (log.isWarnEnabled())
                log.warn("No OwnerLocatorBean found -> dtoMapping principal feature will be ignored.");
        }
        locators.forEach(delegatingLocator::register);
        return delegatingLocator;
    }

    @Bean
    @ConditionalOnMissingBean(DtoClassLocator.class)
    public DtoClassLocator dtoClassLocator(){
        return new RoleFallbackDtoClassLocator();
    }


    @Bean
    @ConditionalOnMissingBean(name = "delegatingDtoClassLocator")
    @Scope(SCOPE_PROTOTYPE)
    public DelegatingDtoClassLocator delegatingDtoClassLocator(DtoClassLocator globalLocator){
        return new DelegatingDtoClassLocator(globalLocator);
    }
}
