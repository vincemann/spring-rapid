package com.github.vincemann.springrapid.auth.slicing.test;

import com.github.vincemann.springrapid.auth.config.*;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

/**
 * Use this annotation to manually import all relevant auto-configuration for testing spring-auth projects.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        RapidAuthGeneralAutoConfiguration.class,
        RapidTokenServiceAutoConfiguration.class,
        RapidUserServiceAutoConfiguration.class,
        RapidUserServiceSecurityAutoConfiguration.class,
        RapidAsyncAutoConfiguration.class,
        RapidAdminAutoConfiguration.class,
        RapidAuthAclAutoConfiguration.class,
        RapidAuthSecurityAutoConfiguration.class
})
@ImportRapidAuthGeneralConfig
public @interface ImportRapidAuthServiceConfig {
}
