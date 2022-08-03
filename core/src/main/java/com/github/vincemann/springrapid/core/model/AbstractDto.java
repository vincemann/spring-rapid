package com.github.vincemann.springrapid.core.model;



// import com.github.vincemann.shortsmartlogger.SmartLogger;

import com.github.vincemann.shortsmartlogger.SmartLogger;

import java.io.Serializable;

public class AbstractDto<Id extends Serializable> extends IdentifiableEntityImpl<Id>{

    @Override
    public String toString() {
        return SmartLogger.builder()
                .build()
                .toString(this);
    }
}
