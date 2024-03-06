package com.github.vincemann.springrapid.core.controller.dto;


import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoRequestInfo;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;

/**
 * Offers a find method, for finding the right Dto class ,for the entity-dto-mapping, in current situation (represented by {@link DtoRequestInfo}).
 */
public interface DtoClassLocator {
    Class<?> find(DtoRequestInfo mappingInfo, DtoMappings context) throws BadEntityException;
}
