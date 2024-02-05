package com.github.vincemann.springrapid.core.proxy.annotation;

import com.github.vincemann.springrapid.core.proxy.ServiceExtension;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DefineProxies.class)
public @interface DefineProxy {

    String name() default "";

    /**
     * bean names
     * use either this or {@link this#extensionClasses()}, not both
     */
    String[] extensions() default {};
    Class<? extends ServiceExtension>[] extensionClasses() default {};
    boolean defaultExtensionsEnabled() default true;

    /**
     * which default extensions should be ignored
     */
    Class<? extends ServiceExtension>[] ignoredExtensions() default {};
}
