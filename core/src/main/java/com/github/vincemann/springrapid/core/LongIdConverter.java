package com.github.vincemann.springrapid.core;

import com.sun.xml.bind.v2.model.core.ID;

import java.io.Serializable;

public class LongIdConverter implements IdConverter<Long> {
    @Override
    public Long toId(String id) {
        return Long.valueOf(id);
    }
}
