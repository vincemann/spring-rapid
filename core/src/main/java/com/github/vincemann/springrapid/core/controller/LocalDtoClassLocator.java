package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;

public abstract class LocalDtoClassLocator {
    private DtoMappingContext context;

    void setContext(DtoMappingContext context) {
        this.context = context;
    }

    public abstract boolean supports(DtoRequestInfo info);

    public abstract Class<?> find(DtoRequestInfo mappingInfo);

    DtoMappingContext getContext() {
        return context;
    }
}
