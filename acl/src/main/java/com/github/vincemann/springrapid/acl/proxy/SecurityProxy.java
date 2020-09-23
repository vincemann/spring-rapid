package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.proxy.BasicServiceExtension;

import java.lang.annotation.*;

/**
 * Represents a {@link SecurityExtensionServiceProxy} in {@link ConfigureProxies}.
 * @see Proxy
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SecurityProxy {
    Class<? extends Annotation>[] qualifiers() default {};
    Class<? extends SecurityServiceExtension>[] rules() default {};
    Class<? extends BasicServiceExtension>[] plugins() default {};
    String name() default "";
    boolean primary() default false;
}
