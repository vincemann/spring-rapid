package com.github.vincemann.springrapid.coretest.slicing;

import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @see ServiceComponent
 */
@Inherited
@Profile(RapidTestProfiles.SERVICE_TEST)
@Component
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceTestComponent {
}
