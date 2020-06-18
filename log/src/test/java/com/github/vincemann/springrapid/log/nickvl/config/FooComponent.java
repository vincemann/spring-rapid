/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package com.github.vincemann.springrapid.log.nickvl.config;

import com.github.vincemann.springrapid.log.nickvl.annotation.LogDebug;
import com.github.vincemann.springrapid.log.nickvl.annotation.LogPoint;
import org.springframework.stereotype.Component;

/**
 * Simple component, does not implement any interface, has non public method.
 */
@Component
public class FooComponent {

    @LogDebug(LogPoint.OUT)
    void voidMethodZero() {
        // nothing to do
    }

    @LogDebug(LogPoint.OUT)
    public IntHolder intMethodZero() {
        return new IntHolder();
    }

}
