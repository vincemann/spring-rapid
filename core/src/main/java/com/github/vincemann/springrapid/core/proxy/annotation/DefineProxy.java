package com.github.vincemann.springrapid.core.proxy.annotation;

import com.github.vincemann.springrapid.core.proxy.ServiceExtension;

import java.lang.annotation.*;

/**
 * Defines an extension proxy that can be used as a blueprint to create a sub proxy (proxy stub) for {@link CreateProxy} or ({@link AutowireProxy}).
 * The proxy will be an {@link com.github.vincemann.springrapid.core.proxy.ExtensionProxy} and will be created by
 * {@link AnnotationExtensionProxyFactory}.
 *
 * See {@link CreateProxy} and {@link AutowireProxy} for examples.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DefineProxies.class)
public @interface DefineProxy {

    String name() default "";

    /**
     * bean names of {@link ServiceExtension}s.
     * use either this or {@link this#extensionClasses()} but not both at the same time
     */
    String[] extensions() default {};
    Class<? extends ServiceExtension>[] extensionClasses() default {};
    boolean defaultExtensionsEnabled() default true;

    /**
     * which default extensions should be ignored
     */
    Class<? extends ServiceExtension>[] ignoredExtensions() default {};
}
