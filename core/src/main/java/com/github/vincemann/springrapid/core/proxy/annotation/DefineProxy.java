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
     */
    String[] extensions() default {};
    boolean defaultExtensionsEnabled() default true;
}
