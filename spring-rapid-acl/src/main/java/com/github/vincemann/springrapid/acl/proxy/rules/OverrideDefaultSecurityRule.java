package com.github.vincemann.springrapid.acl.proxy.rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate {@link com.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule} method a with this annotation,
 * if {@link com.github.vincemann.springrapid.acl.proxy.rules.DefaultServiceSecurityRule}s method a should not be called.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OverrideDefaultSecurityRule {
}
