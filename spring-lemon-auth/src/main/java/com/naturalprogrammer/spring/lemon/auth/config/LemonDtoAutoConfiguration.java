package com.naturalprogrammer.spring.lemon.auth.config;

import com.naturalprogrammer.spring.lemon.auth.controller.dtoMapper.LemonDtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebConfig
public class LemonDtoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "lemonChildMapper")
    public DtoMapper lemonChildMapper(@Qualifier("defaultDtoMapper") DtoMapper defaultDtoMapper){
        return defaultDtoMapper;
    }

    @Bean
    @ConditionalOnMissingBean(LemonDtoMapper.class)
    public DtoMapper lemonDtoMapper(){
        return new LemonDtoMapper();
    }
}
