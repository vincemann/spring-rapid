package com.github.vincemann.springrapid.acl.service;

import com.github.vincemann.springrapid.acl.proxy.ServiceSecurityProxy;
import com.github.vincemann.springrapid.core.service.ServiceBeanType;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Indicates, that {@link com.github.vincemann.springrapid.core.service.CrudService} annotated with this annotation is secured by {@link ServiceSecurityProxy}.
 * This is used for dependency injection purposes.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("securityProxy")
@Inherited
@ServiceBeanType
public @interface Secured {
}
