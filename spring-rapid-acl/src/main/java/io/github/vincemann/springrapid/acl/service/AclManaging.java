package io.github.vincemann.springrapid.acl.service;

import io.github.vincemann.springrapid.core.service.ServiceBeanType;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Service that does not have restrictions from {@link Secured}, realized with {@link io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule}s,
 * but manages Acl information.
 * This is used for dependency injection purposes.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("aclManaging")
@Inherited
@ServiceBeanType
public @interface AclManaging {
}
