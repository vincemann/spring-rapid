package com.github.vincemann.springrapid.core.advice.log;

import java.lang.annotation.*;

/**
 * Annotate any Implementation's public Method with this annotation to log all interactions with this method.
 * Includes Arguments, return Value.
 * @see LogComponentInteractionAdvice
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogInteraction {
    Level level() default Level.DEBUG;

    public enum Level{
        DEBUG,INFO,WARN,ERROR,TRACE
    }
}
