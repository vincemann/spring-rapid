package io.github.vincemann.springrapid.exceptions.config;

import io.github.vincemann.springrapid.core.slicing.config.WebConfig;
import io.github.vincemann.springrapid.exceptions.exceptionHandler.DtoCrudControllerExceptionHandler;
import io.github.vincemann.springrapid.exceptions.exceptionHandler.DtoCrudControllerExceptionHandlerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@WebConfig
public class ExceptionHandlerAutoConfiguration {

    @ConditionalOnMissingBean(DtoCrudControllerExceptionHandler.class)
    @Bean
    public DtoCrudControllerExceptionHandler dtoCrudControllerExceptionHandler() {
        return new DtoCrudControllerExceptionHandlerImpl();
    }
}

