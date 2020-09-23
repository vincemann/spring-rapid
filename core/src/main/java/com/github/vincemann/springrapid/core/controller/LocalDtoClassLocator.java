package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingInfo;

public abstract class LocalDtoClassLocator {
    private DtoMappingContext context;

    void setContext(DtoMappingContext context) {
        this.context = context;
    }

    public abstract boolean supports(DtoMappingInfo info);

    public abstract Class<?> find(DtoMappingInfo mappingInfo);

    DtoMappingContext getContext() {
        return context;
    }
}
