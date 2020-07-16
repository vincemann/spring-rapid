package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import com.github.vincemann.springrapid.core.proxy.CrudServicePlugin;

import java.lang.annotation.*;

/**
 * Represents a {@link ServiceSecurityProxy} in {@link ConfigureProxies}.
 * @see Proxy
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SecurityProxy {
    Class<? extends Annotation>[] qualifiers() default {};
    Class<? extends ServiceSecurityRule>[] rules() default {};
    Class<? extends CrudServicePlugin>[] plugins() default {};
    String name() default "";
    boolean primary() default false;
}
