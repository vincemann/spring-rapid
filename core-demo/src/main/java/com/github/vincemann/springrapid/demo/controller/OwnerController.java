package com.github.vincemann.springrapid.demo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.*;
import com.github.vincemann.springrapid.demo.dtos.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.demo.dtos.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.demo.dtos.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.demo.dtos.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.demo.model.Owner;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.demo.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;

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
