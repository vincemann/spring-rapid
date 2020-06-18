/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package com.github.vincemann.springrapid.log.nickvl.service;


import com.github.vincemann.springrapid.log.nickvl.annotation.Log;
import com.github.vincemann.springrapid.log.nickvl.Severity;

/**
 * Implements {@link com.github.vincemann.springrapid.log.nickvl.service.BazService}.
 */
@Log(Severity.INFO)
public class GeneralBazService extends AbstractBazService {

    @Override
    public void inImpl(String gFirst, String gSecond) {
        // nothing to do
    }
}
