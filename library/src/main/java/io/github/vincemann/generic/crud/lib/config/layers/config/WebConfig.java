package io.github.vincemann.generic.crud.lib.config.layers.config;

import io.github.vincemann.generic.crud.lib.config.layers.component.WebComponent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Inherited
@Profile("web")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@WebComponent
public @interface WebConfig {
}
