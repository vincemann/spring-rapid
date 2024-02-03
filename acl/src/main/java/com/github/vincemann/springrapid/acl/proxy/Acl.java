package com.github.vincemann.springrapid.acl.proxy;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * Indicates, that {@link com.github.vincemann.springrapid.core.service.CrudService} annotated with this annotation is acl managing
 * -> has some acl extensions applied
 * For example a service proxy, created with {@link com.github.vincemann.springrapid.core.proxy.ExtensionProxy} might have an acl extension
 * saving some acl permission on save.
 * This is used as a qualifier for dependency injection
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("acl")
@Inherited
public @interface Acl {
}
