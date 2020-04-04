package io.github.vincemann.springrapid.exceptions.config;

import io.github.vincemann.springrapid.core.config.layers.config.WebConfig;
import io.github.vincemann.springrapid.exceptions.exceptionHandler.DtoCrudControllerExceptionHandler;
import io.github.vincemann.springrapid.exceptions.exceptionHandler.DtoCrudControllerExceptionHandlerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@WebConfig
public class ExceptionHandlerAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public DtoCrudControllerExceptionHandler dtoCrudControllerExceptionHandler() {
        return new DtoCrudControllerExceptionHandlerImpl();
    }
}

