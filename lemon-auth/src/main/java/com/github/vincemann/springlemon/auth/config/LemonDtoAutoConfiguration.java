package com.github.vincemann.springlemon.auth.config;

import com.github.vincemann.springlemon.auth.controller.dtoMapper.LemonDtoPostProcessor;
import com.github.vincemann.springrapid.core.config.RapidDtoMapperAutoConfiguration;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoPostProcessor;
import com.github.vincemann.springrapid.core.slicing.config.WebConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebConfig
@AutoConfigureBefore(RapidDtoMapperAutoConfiguration.class)
public class LemonDtoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LemonDtoPostProcessor.class)
    public DtoPostProcessor lemonDtoPostProcessor(){
        return new LemonDtoPostProcessor();
    }
}
