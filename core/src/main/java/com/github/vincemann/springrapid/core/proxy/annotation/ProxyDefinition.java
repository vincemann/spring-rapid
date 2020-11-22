package com.github.vincemann.springrapid.core.proxy.annotation;

import com.github.vincemann.springrapid.core.proxy.AbstractServiceExtension;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(ProxyDefinitions.class)
public @interface ProxyDefinition {

    String name() default "";
    Class<? extends AbstractServiceExtension>[] extensions() default {};
}
