package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.dto.DtoClassLocator;
import com.github.vincemann.springrapid.core.controller.dto.DtoClassLocatorImpl;
import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocatorImpl;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class RapidDtoLocatorAutoConfiguration {


    @Bean
    @Primary
    @ConditionalOnMissingBean(DelegatingOwnerLocator.class)
    public DelegatingOwnerLocator delegatingOwnerLocator() {
        return new DelegatingOwnerLocatorImpl();
    }

    @Bean
    @ConditionalOnBean(DelegatingOwnerLocator.class)
    public ApplicationListener<ContextRefreshedEvent> ownerLocatorRegistrar(List<OwnerLocator> ownerLocators, DelegatingOwnerLocator delegatingLocator) {
        return event -> {
            if (ownerLocators.size() <= 1){
                if (log.isWarnEnabled())
                    log.warn("No OwnerLocator bean found -> dtoMapping principal feature will be ignored.");
            }
            ownerLocators.forEach(delegatingLocator::register);
        };
    }


    @Bean
    @ConditionalOnMissingBean(DtoClassLocator.class)
    public DtoClassLocator dtoClassLocator(){
        return new DtoClassLocatorImpl();
    }


}
