package io.github.vincemann.springrapid.acl.service;

import io.github.vincemann.springrapid.core.service.ServiceBeanType;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Indicates, that {@link io.github.vincemann.springrapid.core.service.CrudService} annotated with this annotation is not proxied.
 * This is used for dependency injection purposes.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("noProxy")
@Inherited
@ServiceBeanType
public @interface NoProxy {
}
