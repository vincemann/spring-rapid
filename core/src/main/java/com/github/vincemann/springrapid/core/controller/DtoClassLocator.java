package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogException;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingInfo;

/**
 * Offers a find method, for finding the right Dto class ,for the entity-dto-mapping, in current situation (represented by {@link DtoMappingInfo}).
 */
@LogInteraction
public interface DtoClassLocator extends AopLoggable {
    Class<?> find(DtoMappingInfo mappingInfo, DtoMappingContext context);
}
