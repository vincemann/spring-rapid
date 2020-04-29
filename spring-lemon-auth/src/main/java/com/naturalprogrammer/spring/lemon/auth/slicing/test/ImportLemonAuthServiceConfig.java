package com.naturalprogrammer.spring.lemon.auth.slicing.test;

import com.naturalprogrammer.spring.lemon.auth.config.LemonAutoConfiguration;
import com.naturalprogrammer.spring.lemon.auth.config.LemonCommonsAutoConfiguration;
import com.naturalprogrammer.spring.lemon.auth.config.LemonCommonsJpaAutoConfiguration;
import com.naturalprogrammer.spring.lemon.auth.config.LemonServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        LemonAutoConfiguration.class, LemonCommonsAutoConfiguration.class, LemonCommonsJpaAutoConfiguration.class, LemonServiceAutoConfiguration.class
})
public @interface ImportLemonAuthServiceConfig {
}
