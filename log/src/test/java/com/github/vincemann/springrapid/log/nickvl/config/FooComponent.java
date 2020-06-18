/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package com.github.vincemann.springrapid.log.nickvl.config;

import com.github.vincemann.springrapid.log.nickvl.annotation.Log;
import com.github.vincemann.springrapid.log.nickvl.annotation.LogPoint;
import org.springframework.stereotype.Component;

/**
 * Simple component, does not implement any interface, has non public method.
 */
@Component
public class FooComponent {

    @Log(logPoint = LogPoint.OUT)
    void voidMethodZero() {
        // nothing to do
    }

    @Log(logPoint = LogPoint.OUT)
    public IntHolder intMethodZero() {
        return new IntHolder();
    }

}
