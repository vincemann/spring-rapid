package com.naturalprogrammer.spring.lemon.auth.config;

import com.naturalprogrammer.spring.lemon.auth.controller.dtoMapper.LemonDtoPostProcessor;
import io.github.vincemann.springrapid.core.config.DtoMapperAutoConfiguration;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoPostProcessor;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebConfig
@AutoConfigureBefore(DtoMapperAutoConfiguration.class)
public class LemonDtoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LemonDtoPostProcessor.class)
    public DtoPostProcessor lemonDtoPostProcessor(){
        return new LemonDtoPostProcessor();
    }
}
