package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.*;
import com.github.vincemann.springrapid.coredemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import com.github.vincemann.springrapid.coredemo.service.filter.*;
import com.github.vincemann.springrapid.coredemo.service.sort.LastNameAscSorting;
import com.github.vincemann.springrapid.coredemo.service.sort.LastNameDescSorting;
import org.springframework.beans.factory.annotation.Autowired;

@WebController
public class OwnerController extends CrudController<Owner, Long, OwnerService> {


    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder

                .forEndpoint(getCreateUrl(),CreateOwnerDto.class)
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

                // if you can update or create you can read all of the data
                .forEndpoint(getCreateUrl(),Direction.RESPONSE,ReadOwnOwnerDto.class)
                .forEndpoint(getUpdateUrl(),Direction.RESPONSE,ReadOwnOwnerDto.class)

                .build();
    }


    @Autowired
    public void configureAllowedFilters(CityPrefixFilter cityPrefixFilter, HasPetsFilter hasPetsFilter, OwnerTelNumberFilter ownerTelNumberFilter, PetNameEndsWithFilter parentFilter) {
        addAllowedExtensions(cityPrefixFilter,hasPetsFilter,ownerTelNumberFilter,parentFilter);
    }

    @Autowired
    public void configureAllowedSorting(LastNameAscSorting lastNameAscSorting, LastNameDescSorting lastNameDescSorting) {
        addAllowedExtensions(lastNameAscSorting, lastNameDescSorting);
    }

}
