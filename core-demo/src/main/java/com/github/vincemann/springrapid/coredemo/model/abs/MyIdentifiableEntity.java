package com.github.vincemann.springrapid.coredemo.model.abs;

import com.github.vincemann.smartlogger.SmartLogger;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
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
