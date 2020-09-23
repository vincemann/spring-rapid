package com.github.vincemann.springlemon.auth.slicing.test;

import com.github.vincemann.springlemon.auth.config.*;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

/**
 * Use this annotation to manually import all relevant auto-configuration for testing spring-lemon-auth projects.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        LemonAutoConfiguration.class,
        LemonCommonsAutoConfiguration.class,
        LemonCommonsJpaAutoConfiguration.class,
        UserServiceAutoConfiguration.class,
        LemonAsyncAutoConfiguration.class,
        LemonAdminAutoConfiguration.class,
})
@ImportLemonAuthGeneralConfig
public @interface ImportLemonAuthServiceConfig {
}
