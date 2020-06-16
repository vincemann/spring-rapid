package com.github.vincemann.springrapid.core.advice.log;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Annotate any Implementation's public Method with this annotation to log all interactions with this method.
 * Includes Arguments, return Value.
 * @see LogInteractionAdvice
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogInteraction {
    @AliasFor("value")
    Level level() default Level.DEBUG;
    @AliasFor("level")
    Level value() default Level.DEBUG;

    public enum Level{
        DEBUG,INFO,WARN,ERROR,TRACE
    }
}
