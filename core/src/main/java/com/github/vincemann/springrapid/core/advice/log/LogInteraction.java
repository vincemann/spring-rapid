package com.github.vincemann.springrapid.core.advice.log;

import java.lang.annotation.*;

/**
 * Annotate any Implementations Method with this annotation to log all interactions with this method.
 * Includes Arguments, return Value.
 * @see LogComponentInteractionAdvice
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface LogInteraction {
}
