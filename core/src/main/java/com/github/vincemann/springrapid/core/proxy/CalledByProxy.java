package com.github.vincemann.springrapid.core.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that method is called by Framework Proxy. ({@link com.github.vincemann.springrapid.core.proxy.invocationHandler.CrudServicePluginProxy or other spring-rapid-proxies}).
 * It is not necessary to use this annotation, it is just here for better understanding of the code.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CalledByProxy {
}
