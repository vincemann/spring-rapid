package com.github.vincemann.springrapid.core.util;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OtherConfigPresentCondition.class)
public @interface ConditionalOtherConfig {
    String value(); // Specify the fully qualified name of the other configuration class
}