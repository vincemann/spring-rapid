package com.github.vincemann.springrapid.coretest.slicing;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.annotation.*;

/**
 * @see ServiceComponent
 */
@Inherited
@Profile(RapidTestProfiles.SERVICE_TEST)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
@ServiceTestComponent
public @interface ServiceTestConfig {
}
