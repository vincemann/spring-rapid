/***********************************************************************************
 * Copyright (c) 2013. Nickolay Gerilovich. Russia.
 *   Some Rights Reserved.
 ************************************************************************************/

package com.github.vincemann.springrapid.log.nickvl;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.NullAppender;

import java.util.ArrayList;
import java.util.List;

/**
 * Extended {@link NullAppender} intercepts log output.
 */
public class StubAppender extends NullAppender {
    private static final List<String> messages = new ArrayList<String>();

    @Override
    public void doAppend(LoggingEvent event) {
        messages.add(event.getMessage().toString());
    }

    public static List<String> clear() {
        List<String> copy = new ArrayList<String>(messages);
        messages.clear();
        return copy;
    }
}
