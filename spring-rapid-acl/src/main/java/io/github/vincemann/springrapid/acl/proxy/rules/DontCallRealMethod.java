package io.github.vincemann.springrapid.acl.proxy.rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate {@link io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule} method with this annotation,
 * if the rule decides, that the underlying intercepted method should not be called.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DontCallRealMethod {
}
