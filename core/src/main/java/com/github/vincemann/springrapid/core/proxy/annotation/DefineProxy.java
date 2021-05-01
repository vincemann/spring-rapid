package com.github.vincemann.springrapid.core.proxy.annotation;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DefineProxies.class)
public @interface DefineProxy {

    String name() default "";
    Class<? extends AbstractServiceExtension>[] extensions() default {};
    boolean defaultExtensionsEnabled() default true;
}
