package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.controller.dtoMapper.*;
import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

@WebConfig
@Slf4j
public class DtoMapperAutoConfiguration {

    public DtoMapperAutoConfiguration() {
        log.info("Created");
    }

    @ConditionalOnMissingBean(name = "defaultDtoMapper")
    @Default
    @Bean
    public DtoMapper defaultDtoMapper(){
        return new BasicDtoMapper();
    }


    @Delegating
    @ConditionalOnMissingBean(name = "delegatingDtoMapper")
    @Bean
    public DtoMapper delegatingDtoMapper(List<DtoMapper> dtoMappers, @Default DtoMapper defaultDtoMapper){
        DelegatingDtoMapper delegatingDtoMapper = new DelegatingDtoMapper(defaultDtoMapper);
        dtoMappers.remove(defaultDtoMapper);
        dtoMappers.forEach(delegatingDtoMapper::registerDelegate);
        return delegatingDtoMapper;
    }
}
