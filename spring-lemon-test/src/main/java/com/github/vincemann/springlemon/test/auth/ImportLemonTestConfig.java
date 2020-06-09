package com.github.vincemann.springlemon.test.auth;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        LemonTestAutoConfiguration.class
})
public @interface ImportLemonTestConfig {
}
