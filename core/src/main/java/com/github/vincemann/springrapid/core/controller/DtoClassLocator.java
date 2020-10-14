package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;

/**
 * Offers a find method, for finding the right Dto class ,for the entity-dto-mapping, in current situation (represented by {@link DtoRequestInfo}).
 */
@LogInteraction
public interface DtoClassLocator extends AopLoggable {
    Class<?> find(DtoRequestInfo mappingInfo, DtoMappingContext context);
}
