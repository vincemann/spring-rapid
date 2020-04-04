package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.config.layers.config.WebConfig;
import io.github.vincemann.springrapid.core.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(CrudServiceLocatorConfig.class)
@WebConfig
public class DtoMapperConfig {

    @Qualifier("default")
    @Bean
    public DtoMapper defaultDtoMapper(){
        return new BasicDtoMapper();
    }

}
