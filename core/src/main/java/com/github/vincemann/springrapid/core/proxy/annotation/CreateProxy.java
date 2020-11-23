package com.github.vincemann.springrapid.core.proxy.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CreateProxies.class)
public @interface CreateProxy {
    String[] proxies() default {};
    Class<? extends Annotation>[] qualifiers();
    String name() default "";
    boolean primary() default false;
}
