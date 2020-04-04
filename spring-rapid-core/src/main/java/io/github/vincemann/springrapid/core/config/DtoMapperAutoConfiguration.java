package io.github.vincemann.springrapid.core.config;

import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import io.github.vincemann.springrapid.core.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.springrapid.core.controller.dtoMapper.DtoMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(CrudServiceLocatorAutoConfiguration.class)
@WebConfig
public class DtoMapperAutoConfiguration {

    @Qualifier("default")
    @Bean
    public DtoMapper defaultDtoMapper(){
        return new BasicDtoMapper();
    }

}
