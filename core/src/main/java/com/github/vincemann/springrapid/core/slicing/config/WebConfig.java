package com.github.vincemann.springrapid.core.slicing.config;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.slicing.components.WebComponent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.annotation.*;

/**
 * @see ServiceComponent
 */
@Inherited
@Profile("web")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@WebComponent
public @interface WebConfig {
}
