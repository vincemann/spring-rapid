package com.github.vincemann.springlemon.auth.slicing.test;

import com.github.vincemann.springlemon.auth.config.RapidAuthExceptionHandlerAutoConfiguration;
import com.github.vincemann.springlemon.auth.config.RapidCommonsWebAutoConfiguration;
import com.github.vincemann.springlemon.auth.config.RapidAuthenticationAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        RapidAuthExceptionHandlerAutoConfiguration.class,
        RapidCommonsWebAutoConfiguration.class,
        RapidAuthenticationAutoConfiguration.class
})
@ImportRapidAuthGeneralConfig
public @interface ImportRapidAuthWebConfig {
}
