package com.github.vincemann.springrapid.acl.proxy;

import com.github.vincemann.springrapid.core.proxy.CrudServicePluginProxyFactory;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;


/**
 * Annotate your {@link com.github.vincemann.springrapid.core.service.CrudService} with this annotation to
 * induce the dynamic creation of Proxies for a {@link com.github.vincemann.springrapid.core.service.CrudService}.
 * This is an alternative solution for {@link CrudServiceSecurityProxyFactory}
 * and {@link CrudServicePluginProxyFactory}.
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
