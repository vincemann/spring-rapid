package io.github.vincemann.springrapid.acl.proxy.rules;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier("defaultServiceSecurityRule")
@Inherited
public @interface DefaultServiceSecurityRule {
}
