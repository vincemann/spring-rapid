package com.naturalprogrammer.spring.lemon.auth.slicing.test;

import com.naturalprogrammer.spring.lemon.auth.config.*;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        LemonAuthExceptionHandlerAutoConfiguration.class,
        LemonCommonsWebAutoConfiguration.class,
        LemonWebAutoConfiguration.class
})
@ImportLemonAuthGeneralConfig
public @interface ImportLemonAuthWebConfig {
}
