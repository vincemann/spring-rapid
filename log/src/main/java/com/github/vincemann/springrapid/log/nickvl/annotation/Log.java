/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package com.github.vincemann.springrapid.log.nickvl.annotation;

import com.github.vincemann.springrapid.log.nickvl.Severity;
import org.springframework.core.annotation.AliasFor;


import java.lang.annotation.*;

/**
 * Meta annotation that indicates a log method annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Repeatable(Log.class)
public @interface Log {

    @AliasFor("level")
    Severity value() default Severity.DEBUG;

    @AliasFor("value")
    Severity level() default Severity.DEBUG;

    boolean disabled() default false;
    /**
     * A part of the method to be logged. {@link LogPoint#BOTH} by default.
     */
    LogPoint logPoint() default LogPoint.BOTH;

}