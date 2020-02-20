package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.controller.dtoMapper.BasicDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.EntityIdResolver;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.idResolver.IdResolvingDtoMapper;
import io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler.DtoCrudControllerExceptionHandlerImpl;
import io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler.DtoCrudControllerExceptionHandler;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.EndpointsExposureContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DtoCrudControllerConfig {

    @Bean
    public DtoCrudControllerExceptionHandler dtoCrudControllerExceptionHandler() {
        return new DtoCrudControllerExceptionHandlerImpl();
    }


    @Qualifier("default")
    @Bean
    public DtoMapper defaultDtoMapper(){
        return new BasicDtoMapper();
    }
}
