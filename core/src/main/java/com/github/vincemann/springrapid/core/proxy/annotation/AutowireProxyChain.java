package com.github.vincemann.springrapid.core.proxy.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to autowire dynamically created proxy chains.
 * Dont combine with {@link Autowired}, use as a replacement.
 * Only setter and field injection is supported.
 * Proxies may be created with {@link DefineProxy} or as normal spring beans.
 * This is an alternative approach to using {@link CreateProxy}.
 *
 * example:
 *
 * @DefineProxy(name = "acl", extensions = ...)
 * @DefineProxy(name = "secured", extensions = ...)
 * class MyService {
 *     ...
 * }
 *
 * class SomeOtherClass{
 *
 *     @AutowiredProxyChain(value = {"acl","secured"})
 *     private MyService service;
 *
 * }
 *
 * This would create a new proxy instance secured -> acl -> root and set to field 'service'.
 *
 *
 * See {@link com.github.vincemann.springrapid.core.proxy.ExtensionProxy} and {@link AnnotationExtensionProxyFactory}.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface AutowireProxyChain {
    /**
     * Contains the proxy names in the order of chaining.
     * Can be either the {@link DefineProxy#name()} or bean name.
     */
    String[] value();
}

