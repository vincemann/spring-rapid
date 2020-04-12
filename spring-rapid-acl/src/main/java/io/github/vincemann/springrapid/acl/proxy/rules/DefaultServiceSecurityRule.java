package io.github.vincemann.springrapid.acl.proxy.rules;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * The rule Qualified by this annotation will always be called after all {@link ServiceSecurityRule}s have been called.
 * The only exception for execution of method a in default Rule is, when the method a inside the securityRule is annotated with @{@link OverrideDefaultSecurityRule}.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("defaultServiceSecurityRule")
@Inherited
public @interface DefaultServiceSecurityRule {
}
