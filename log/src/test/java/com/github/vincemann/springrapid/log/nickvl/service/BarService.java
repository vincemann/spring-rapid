/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package com.github.vincemann.springrapid.log.nickvl.service;


import com.github.vincemann.springrapid.log.nickvl.annotation.Log;
import com.github.vincemann.springrapid.log.nickvl.Severity;

/**
 * Bar service interface.
 */
public interface BarService {

    @Log(Severity.WARN)
    void inExtendedLogInSuperOnly(String iFirst, String iSecond);

    void inAbstract(String iFirst, String iSecond);

    void inExtended(String iFirst, String iSecond);

    void overridden(String iFirst, String iSecond);

    void overriddenLogInAbstractOnly(String iFirst, String iSecond);
}
