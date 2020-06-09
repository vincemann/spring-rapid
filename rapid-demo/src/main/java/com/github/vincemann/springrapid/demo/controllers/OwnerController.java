package com.github.vincemann.springrapid.demo.controllers;

import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.demo.dtos.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.demo.dtos.owner.ReadOwnerDto;
import com.github.vincemann.springrapid.demo.dtos.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import com.github.vincemann.springrapid.core.controller.rapid.RapidController;
import com.github.vincemann.springrapid.demo.service.OwnerService;

@WebController
public class OwnerController extends RapidController<Owner, Long, OwnerService> {


    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return DtoMappingContextBuilder.builder()
                .forEndpoint(RapidDtoEndpoint.CREATE, CreateOwnerDto.class)
                .forUpdate(UpdateOwnerDto.class)
                .forResponse(ReadOwnerDto.class)
                .build();
    }
}
