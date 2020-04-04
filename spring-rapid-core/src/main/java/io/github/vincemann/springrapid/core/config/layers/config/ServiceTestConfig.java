package io.github.vincemann.springrapid.core.config.layers.config;

import io.github.vincemann.springrapid.core.config.layers.component.ServiceTestComponent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.annotation.*;

@Inherited
@Profile("serviceTest")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@ServiceTestComponent
public @interface ServiceTestConfig {
}
