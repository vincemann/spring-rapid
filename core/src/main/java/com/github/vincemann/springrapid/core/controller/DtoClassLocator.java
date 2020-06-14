package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;

/**
 * Offers a find method, for finding the right Dto class for the current situation (represented by {@link DtoMappingInfo}).
 */
public interface DtoClassLocator {
    Class<?> find(DtoMappingInfo mappingInfo, DtoMappingContext context);
}
