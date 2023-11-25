package com.github.vincemann.springrapid.acldemo.model.abs;

import com.github.vincemann.smartlogger.api.SmartLogger;
import com.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;

import java.io.Serializable;

public class MyIdentifiableEntity<Id extends Serializable> extends IdentifiableEntityImpl<Id> {

    @Override
    public String toString() {
        return SmartLogger.builder()
                .build()
                .toString(this);
    }
}
