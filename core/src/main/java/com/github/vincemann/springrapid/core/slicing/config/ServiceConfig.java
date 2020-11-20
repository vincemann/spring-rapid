package com.github.vincemann.springrapid.core.slicing.config;

import com.github.vincemann.springrapid.core.RapidProfiles;
import com.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.annotation.*;

/**
 * @see ServiceComponent
 */
@Inherited
@Profile(RapidProfiles.SERVICE)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@ServiceComponent
public @interface ServiceConfig {
}
