package com.github.vincemann.springrapid.core.advice.log;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogConfig {
    boolean ignoreGetters() default true;
    boolean ignoreSetters() default true;
}
