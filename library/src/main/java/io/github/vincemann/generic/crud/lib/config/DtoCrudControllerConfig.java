package io.github.vincemann.generic.crud.lib.config;

import io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler.DtoCrudControllerExceptionHandlerImpl;
import io.github.vincemann.generic.crud.lib.controller.errorHandling.exceptionHandler.DtoCrudControllerExceptionHandler;
import org.springframework.context.annotation.Bean;

public class DtoCrudControllerConfig {

    @Bean
    public DtoCrudControllerExceptionHandler dtoCrudControllerExceptionHandler(){
        return new DtoCrudControllerExceptionHandlerImpl();
    }
}
