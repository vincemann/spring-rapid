package com.github.vincemann.springrapid.coredemo.dto.abs;

import com.github.vincemann.smartlogger.SmartLogger;
import com.github.vincemann.springrapid.core.dto.IdAwareDto;

import java.io.Serializable;

public class MyIdDto<Id extends Serializable> extends IdAwareDto<Id> {

    @Override
    public String toString() {
        return SmartLogger.builder()
                .build()
                .toString(this);
    }
}
