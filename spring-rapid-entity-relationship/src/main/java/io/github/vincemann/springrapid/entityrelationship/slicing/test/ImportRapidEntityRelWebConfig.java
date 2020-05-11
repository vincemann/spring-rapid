package io.github.vincemann.springrapid.entityrelationship.slicing.test;

import io.github.vincemann.springrapid.entityrelationship.config.IdResolvingDtoMapperAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration(IdResolvingDtoMapperAutoConfiguration.class)
public @interface ImportRapidEntityRelWebConfig {
}
