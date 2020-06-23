package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.proxy.CrudServicePlugin;
import com.github.vincemann.springrapid.core.proxy.CrudServicePluginProxy;

import java.lang.annotation.*;

/**
 * Represents meta information about a {@link CrudServicePluginProxy}, that can be dynamically created,
 * when used together with {@link ConfigureProxies}.
 *
 * @see ConfigureProxies
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(ConfigureProxies.class)
public @interface Proxy {

    String name() default "";

    Class<? extends Annotation>[] qualifiers() default {};

    Class<? extends CrudServicePlugin>[] plugins() default {};

    boolean primary() default false;


}
