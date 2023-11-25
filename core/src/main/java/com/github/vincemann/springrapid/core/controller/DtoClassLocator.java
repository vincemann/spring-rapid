package com.github.vincemann.springrapid.core.controller;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.aoplog.api.annotation.LogParam;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoRequestInfo;

/**
 * Offers a find method, for finding the right Dto class ,for the entity-dto-mapping, in current situation (represented by {@link DtoRequestInfo}).
 */
@LogInteraction
public interface DtoClassLocator extends AopLoggable {
    Class<?> find(@LogParam DtoRequestInfo mappingInfo, DtoMappingContext context);
}
