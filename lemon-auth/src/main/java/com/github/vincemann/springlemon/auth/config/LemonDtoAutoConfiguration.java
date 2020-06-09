package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.controller.dtoMapper.LemonDtoPostProcessor;
import com.github.vincemann.springrapid.core.config.DtoMapperAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.dtoMapper.DtoPostProcessor;
import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
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
