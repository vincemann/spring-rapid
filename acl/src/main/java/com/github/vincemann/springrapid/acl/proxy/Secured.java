package com.github.vincemann.springrapid.acl.proxy;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Indicates, that {@link com.github.vincemann.springrapid.core.service.CrudService} annotated with this annotation is secured
 * -> has some {@link com.github.vincemann.springrapid.acl.service.ext.sec.SecurityExtension}s applied.
 * For example a service proxy, created with {@link com.github.vincemann.springrapid.core.proxy.ExtensionProxy} might have an security extension
 * checking some for write permission before any update call, like {@link com.github.vincemann.springrapid.acl.service.ext.sec.CrudAclChecksSecurityExtension}.
 * This is used as a qualifier for dependency injection
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("secured")
@Inherited
public @interface Secured {
}
