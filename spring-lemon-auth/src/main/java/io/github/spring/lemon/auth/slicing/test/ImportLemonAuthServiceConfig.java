package io.github.spring.lemon.auth.slicing.test;

import io.github.spring.lemon.auth.config.*;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        LemonAutoConfiguration.class,
        LemonCommonsAutoConfiguration.class,
        LemonCommonsJpaAutoConfiguration.class,
        LemonServiceAutoConfiguration.class,
        LemonAsyncAutoConfiguration.class,
        LemonAdminAutoConfiguration.class
})
@ImportLemonAuthGeneralConfig
public @interface ImportLemonAuthServiceConfig {
}
