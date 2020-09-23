package com.github.vincemann.springrapid.demo.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.*;
import com.github.vincemann.springrapid.demo.dtos.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.demo.dtos.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.demo.dtos.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.demo.dtos.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.RapidController;
import com.github.vincemann.springrapid.demo.service.OwnerService;

@WebController
public class OwnerController extends RapidController<Owner, Long, OwnerService> {


    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return DtoMappingContextBuilder.builder()
                .forEndpoint(RapidDtoEndpoint.CREATE, CreateOwnerDto.class)
                .forUpdate(UpdateOwnerDto.class)
                //response dto config
                //authenticated
                .withPrincipal(DtoMappingInfo.Principal.OWN)
                .forResponse(ReadOwnOwnerDto.class)
                .withPrincipal(DtoMappingInfo.Principal.FOREIGN)
                .forResponse(ReadForeignOwnerDto.class)
                //not authenticated
                .withAllPrincipals()
                .forResponse(ReadForeignOwnerDto.class)
                .build();
    }
}
