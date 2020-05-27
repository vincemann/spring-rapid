package io.github.spring.lemon.auth.slicing.test;

import io.github.spring.lemon.auth.config.LemonAuthExceptionHandlerAutoConfiguration;
import io.github.spring.lemon.auth.config.LemonCommonsWebAutoConfiguration;
import io.github.spring.lemon.auth.config.LemonWebAutoConfiguration;
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
