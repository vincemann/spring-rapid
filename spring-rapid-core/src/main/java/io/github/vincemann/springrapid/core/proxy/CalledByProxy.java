package io.github.vincemann.springrapid.core.proxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that method is called by Framework Proxy.
 * It is not necessary to use this annotation, it is just here for better understanding of code
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CalledByProxy {
}
