package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.*;
import com.github.vincemann.springrapid.authdemo.dtos.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.authdemo.dtos.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.authdemo.dtos.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.authdemo.dtos.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.authdemo.model.Owner;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.authdemo.service.OwnerService;

@WebController
public class OwnerController extends CrudController<Owner, Long, OwnerService> {


    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder
                .forEndpoint(getCreateUrl(), CreateOwnerDto.class)
                .forUpdate(UpdateOwnerDto.class)
                //response dto config
                //authenticated
                .withPrincipal(DtoRequestInfo.Principal.OWN)
                .forResponse(ReadOwnOwnerDto.class)
                .withPrincipal(DtoRequestInfo.Principal.FOREIGN)
                .forResponse(ReadForeignOwnerDto.class)
                //not authenticated
                .withAllPrincipals()
                .forResponse(ReadForeignOwnerDto.class)
                .build();
    }

}
