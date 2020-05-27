package io.github.spring.lemon.auth.slicing.test;

import io.github.spring.lemon.auth.config.LemonGeneralAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration({
        LemonGeneralAutoConfiguration.class
})
public @interface ImportLemonAuthGeneralConfig {
}
