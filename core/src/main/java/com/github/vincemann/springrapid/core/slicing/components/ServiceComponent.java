package com.github.vincemann.springrapid.core.slicing.components;

import com.github.vincemann.springrapid.core.RapidProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Annotate Service beans- ,interfaces and -abstractClasses with this annotation to mark them as ServiceComponents.
 * This is used for Slicing the Application Context and can be used to create multiple Contexts for different test - scenarios,
 * where only certain groups of beans are necessary. (ServiceBeans, WebBeans, TestBeans, ServiceTestBeans, WebTestBeans)
 * Also useful auto-mocking all Beans of a certain group/ slice.
 * See spring-rapid-core-test package for more.
 */
@Inherited
@Profile(RapidProfiles.SERVICE)
@Component
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceComponent {
}
