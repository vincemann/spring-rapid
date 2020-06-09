package com.github.vincemann.springrapid.acl.proxy.rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate {@link com.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule} method with this annotation,
 * if the underlying intercepted (target-) method should not be called.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DontCallTargetMethod {
}
