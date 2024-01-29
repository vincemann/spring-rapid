package com.github.vincemann.springrapid.core.controller.dto;

import com.github.vincemann.aoplog.api.AopLoggable;
import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.aoplog.api.annotation.LogParam;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoRequestInfo;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

/**
 * Offers a find method, for finding the right Dto class ,for the entity-dto-mapping, in current situation (represented by {@link DtoRequestInfo}).
 */
@LogInteraction
public interface DtoClassLocator extends AopLoggable {
    Class<?> find(@LogParam DtoRequestInfo mappingInfo, DtoMappings context) throws BadEntityException;
}
