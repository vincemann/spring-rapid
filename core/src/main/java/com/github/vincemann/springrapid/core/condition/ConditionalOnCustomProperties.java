package com.github.vincemann.springrapid.core.condition;

import com.github.vincemann.springrapid.core.util.CustomPropertyCondition;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(CustomPropertyCondition.class)
public @interface ConditionalOnCustomProperties {
    String[] properties();
}
