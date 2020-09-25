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
        LemonGeneralAutoConfiguration.class,
        LemonTokenAutoConfiguration.class,
        UserServiceAutoConfiguration.class,
        UserServiceSecurityAutoConfiguration.class,
        LemonAsyncAutoConfiguration.class,
        LemonAdminAutoConfiguration.class,
        LemonAclAutoConfiguration.class,
        LemonSecurityAutoConfiguration.class
})
@ImportLemonAuthGeneralConfig
public @interface ImportLemonAuthServiceConfig {
}
