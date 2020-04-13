package io.github.vincemann.springrapid.acl.proxy;

import io.github.vincemann.springrapid.acl.proxy.rules.ServiceSecurityRule;
import io.github.vincemann.springrapid.core.service.plugin.CrudServicePlugin;

import java.lang.annotation.*;

/**
 * Represents a {@link CrudServiceSecurityProxy} in {@link io.github.vincemann.springrapid.acl.proxy.create.ConfigureProxies}.
 * @see io.github.vincemann.springrapid.acl.proxy.create.Proxy
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
