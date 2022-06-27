package com.github.vincemann.springrapid.core.model;

import com.github.vincemann.springrapid.core.util.LazyLogger;

import java.io.Serializable;

public class AbstractDto<Id extends Serializable> extends IdentifiableEntityImpl<Id>{

    @Override
    public String toString() {
        return LazyLogger.builder()
                .build()
                .toString(this);
    }
}
