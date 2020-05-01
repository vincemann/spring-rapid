package com.naturalprogrammer.spring.lemon.auth.slicing.test;

import com.naturalprogrammer.spring.lemon.auth.config.*;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        LemonAutoConfiguration.class, LemonCommonsAutoConfiguration.class, LemonCommonsJpaAutoConfiguration.class, LemonServiceAutoConfiguration.class, LemonAsyncAutoConfiguration.class
})
@ImportLemonAuthGeneralConfig
public @interface ImportLemonAuthServiceConfig {
}
