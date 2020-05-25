package io.github.vincemann.springrapid.core.proxy;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplyIfRole {

    String[] isNot() default {};
    String[] is() default {};
    @AliasFor("is")
    String[] value() default {};
}
