package com.github.vincemann.springrapid.auth.config;

import com.github.vincemann.springrapid.auth.controller.UserDtoPostProcessor;
import com.github.vincemann.springrapid.core.config.RapidDtoMapperAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Configuration
@AutoConfigureBefore(RapidDtoMapperAutoConfiguration.class)
public class RapidAuthDtoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(UserDtoPostProcessor.class)
    public UserDtoPostProcessor userDtoPostProcessor(){
        return new UserDtoPostProcessor();
    }



}
