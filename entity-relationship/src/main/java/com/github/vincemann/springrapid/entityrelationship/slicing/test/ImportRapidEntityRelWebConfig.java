package com.github.vincemann.springrapid.entityrelationship.slicing.test;

import com.github.vincemann.springrapid.entityrelationship.config.RapidIdResolvingDtoMapperAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration(RapidIdResolvingDtoMapperAutoConfiguration.class)
public @interface ImportRapidEntityRelWebConfig {
}
