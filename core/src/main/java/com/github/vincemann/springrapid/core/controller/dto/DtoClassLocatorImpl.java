package com.github.vincemann.springrapid.core.controller.dto;

import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoRequestInfo;
import com.github.vincemann.springrapid.core.controller.dto.map.Mapping;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

public class DtoClassLocatorImpl implements DtoClassLocator {
    @Override
    public Class<?> find(DtoRequestInfo request, DtoMappings mappings) throws BadEntityException {
        for (Mapping mapping : mappings.get()) {
            if (mapping.getCondition().test(request)) {
                if (mapping.getDtoClass() != null) {
                    return mapping.getDtoClass();
                }
            }
        }
        throw new BadEntityException("No matching DTO found for the given request");
    }
}
