package io.github.vincemann.springrapid.core.slicing.config;

import io.github.vincemann.springrapid.core.slicing.components.WebTestComponent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.annotation.*;

@Inherited
@Profile("webTest")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@WebTestComponent
public @interface WebTestConfig {
}
