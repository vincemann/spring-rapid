package com.github.vincemann.springrapid.core.util.condition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(CustomPropertyCondition.class)
public @interface ConditionalOnCustomProperties {
    String[] properties();
}
