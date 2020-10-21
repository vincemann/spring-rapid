package com.github.vincemann.springrapid.auth.slicing.test;

import com.github.vincemann.springrapid.auth.config.RapidAuthExceptionHandlerAutoConfiguration;
import com.github.vincemann.springrapid.auth.config.RapidCommonsWebAutoConfiguration;
import com.github.vincemann.springrapid.auth.config.RapidAuthenticationAutoConfiguration;
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
