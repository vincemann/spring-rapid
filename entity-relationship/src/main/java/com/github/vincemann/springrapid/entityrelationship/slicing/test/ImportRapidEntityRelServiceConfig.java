package com.github.vincemann.springrapid.entityrelationship.slicing.test;

import com.github.vincemann.springrapid.entityrelationship.config.RapidBiDirServiceAdviceAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration(RapidBiDirServiceAdviceAutoConfiguration.class)
public @interface ImportRapidEntityRelServiceConfig {
}
