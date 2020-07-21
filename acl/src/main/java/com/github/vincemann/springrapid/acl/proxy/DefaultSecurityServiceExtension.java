package com.github.vincemann.springrapid.acl.proxy;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

/**
 * The rule Qualified by this annotation will always be called after all {@link SecurityServiceExtension}s have been called.
 * The only exception for execution of method a in default Rule is, when {@link SecurityProxyController#overrideDefaultExtension()} is called by Extension.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("defaultServiceSecurityRule")
@Inherited
public @interface DefaultSecurityServiceExtension {
}
