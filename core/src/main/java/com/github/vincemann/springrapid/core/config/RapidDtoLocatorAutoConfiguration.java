package com.github.vincemann.springrapid.core.config;

import com.github.vincemann.springrapid.core.controller.dto.DtoClassLocator;
import com.github.vincemann.springrapid.core.controller.dto.DtoClassLocatorImpl;
import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.controller.owner.OwnerLocator;
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
    @ConditionalOnMissingBean(name = "delegatingOwnerLocator")
    public DelegatingOwnerLocator delegatingOwnerLocator() {
        return new DelegatingOwnerLocator();
    }

    @Bean
    public ApplicationListener<ContextRefreshedEvent> ownerLocatorRegistrar(ApplicationContext context, DelegatingOwnerLocator delegatingLocator) {
        return event -> {
            Map<String, OwnerLocator> locators = context.getBeansOfType(OwnerLocator.class);
            if (locators.size() <= 1){
                if (log.isWarnEnabled())
                    log.warn("No OwnerLocator bean found -> dtoMapping principal feature will be ignored.");
            }
            locators.values().forEach(locator -> {
                if (locator != delegatingLocator) {
                    delegatingLocator.register(locator);
                }
            });
        };
    }


    @Bean
    @ConditionalOnMissingBean(DtoClassLocator.class)
    public DtoClassLocator dtoClassLocator(){
        return new DtoClassLocatorImpl();
    }


}
