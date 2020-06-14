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
    boolean args() default true;
    boolean result() default true;
    Level level() default Level.DEBUG;

    /**
     * How much should the logging block be indented?
     * This is useful for creating an optical calling hierarchy.
     * I.e. :
     * class DelegatingFoo {
     *     @LogInteraction
     *     public Result find(Args){
     *         delegateBar.find(diffArgs);
     *     }
     * }
     *
     * class DelegateBar {
     *       @LogInteraction(indentBy=1)
     *       public Result find(diffArgs){
     *           ...
     *       }
     * }
     *
     * This would look something like this:
     * ----------------------------------
     * Input for DelegatingFoo: args
     *
     *          ----------------------------------
     *          Input for DelegateBar: diffArgs
     *          Result for DelegateBar: stuff
     *          ====================================
     *
     * Result for DelegatingFoo: diffStuff
     * ==================================
     *
     */
    int indentBy() default 0;

    public enum Level{
        DEBUG,INFO,WARN,ERROR
    }
}
