package com.github.vincemann.springrapid.core.service.id;

import java.io.Serializable;

public class LongIdConverter implements IdConverter<Long> {
    @Override
    public Long toId(String id) {
        return Long.valueOf(id);
    }

    @Override
    public Long getUnknownId() {
        return -1L;
    }

    @Override
    public Class<? extends Serializable> getIdType() {
        return Long.class;
    }
}
