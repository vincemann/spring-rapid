package io.github.vincemann.springrapid.acl.proxy.create;

import io.github.vincemann.springrapid.acl.proxy.SecurityProxy;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;


/**
 * This annotation can be used to create and configure Proxies for a {@link io.github.vincemann.springrapid.core.service.CrudService}.
 * This is an alternative solution for {@link io.github.vincemann.springrapid.acl.proxy.create.CrudServiceSecurityProxyFactory}
 * and {@link io.github.vincemann.springrapid.core.proxy.factory.CrudServicePluginProxyFactory}.
 *
 * The {@link CrudServiceProxyBeanComposer} will detect the configuration and create the proxies at runtime.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConfigureProxies {
    @AliasFor("value")
    Proxy[] proxies() default {};
    @AliasFor("proxies")
    Proxy[] value() default {};
    SecurityProxy[] securityProxies() default {};
}
