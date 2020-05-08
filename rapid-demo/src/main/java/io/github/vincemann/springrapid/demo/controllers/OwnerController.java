package io.github.vincemann.springrapid.demo.controllers;

import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.demo.dtos.owner.CreateOwnerDto;
import io.github.vincemann.springrapid.demo.dtos.owner.ReadOwnerDto;
import io.github.vincemann.springrapid.demo.dtos.owner.UpdateOwnerDto;
import io.github.vincemann.springrapid.demo.model.Owner;
import io.github.vincemann.springrapid.core.slicing.components.WebController;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;
import io.github.vincemann.springrapid.demo.service.OwnerService;

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
