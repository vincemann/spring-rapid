package com.github.vincemann.springrapid.acl.proxy.create;

import com.github.vincemann.springrapid.acl.proxy.SecurityProxy;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;


/**
 * Annotate your {@link com.github.vincemann.springrapid.core.service.CrudService} with this annotation to
 * induce the dynamic creation of Proxies for a {@link com.github.vincemann.springrapid.core.service.CrudService}.
 * This is an alternative solution for {@link com.github.vincemann.springrapid.acl.proxy.create.CrudServiceSecurityProxyFactory}
 * and {@link com.github.vincemann.springrapid.core.proxy.factory.CrudServicePluginProxyFactory}.
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
