package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.service.ServiceBeanType;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Indicates, that {@link com.github.vincemann.springrapid.core.service.CrudService} annotated with this annotation is not proxied, thus unsecured.
 * This is the simplest default unsecured and not @{@link AclManaging} version of your service.
 *
 * Put your security logic into a @{@link Secured} proxy.
 *
 * This annotation is used for dependency injection purposes.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("unsecured")
@Inherited
@ServiceBeanType
public @interface Unsecured {
}
