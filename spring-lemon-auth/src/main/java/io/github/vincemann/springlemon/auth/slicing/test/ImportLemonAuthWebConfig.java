package io.github.vincemann.springlemon.auth.slicing.test;

import io.github.vincemann.springlemon.auth.config.LemonAuthExceptionHandlerAutoConfiguration;
import io.github.vincemann.springlemon.auth.config.LemonCommonsWebAutoConfiguration;
import io.github.vincemann.springlemon.auth.config.LemonWebAutoConfiguration;
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
