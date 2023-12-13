package com.github.vincemann.springrapid.core.proxy.annotation;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;

import java.lang.annotation.*;
import java.util.Map;

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
    Class[] extensionClasses() default {};
    boolean defaultExtensionsEnabled() default true;

    /**
     * which default extensions should be ignored
     */
    Class<? extends AbstractServiceExtension>[] ignoredExtensions() default {};
}
