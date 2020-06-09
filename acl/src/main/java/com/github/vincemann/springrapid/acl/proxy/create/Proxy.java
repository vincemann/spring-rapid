package com.github.vincemann.springrapid.acl.proxy.create;

import com.github.vincemann.springrapid.core.proxy.invocationHandler.CrudServicePlugin;

import java.lang.annotation.*;

/**
 * Represents meta information about a {@link com.github.vincemann.springrapid.core.proxy.invocationHandler.CrudServicePluginProxy}, that can be dynamically created,
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
