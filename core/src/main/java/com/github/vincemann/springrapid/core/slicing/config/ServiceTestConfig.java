package com.github.vincemann.springrapid.core.slicing.config;

import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import com.github.vincemann.springrapid.core.slicing.components.ServiceTestComponent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.annotation.*;

/**
 * @see ServiceComponent
 */
@Inherited
@Profile("serviceTest")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@ServiceTestComponent
public @interface ServiceTestConfig {
}
