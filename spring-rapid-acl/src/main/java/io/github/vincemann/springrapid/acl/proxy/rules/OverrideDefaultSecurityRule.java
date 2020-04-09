package io.github.vincemann.springrapid.acl.proxy.rules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate {@link io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule} method with this annotation,
 * if rule overrides the {@link io.github.vincemann.springrapid.acl.proxy.rules.DefaultServiceSecurityRule}s method -> default security rule method
 * wont be called.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OverrideDefaultSecurityRule {
}
