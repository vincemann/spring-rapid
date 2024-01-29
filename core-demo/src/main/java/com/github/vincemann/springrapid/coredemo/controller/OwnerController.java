package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.Principal;
import com.github.vincemann.springrapid.coredemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.core.slicing.WebController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import static com.github.vincemann.springrapid.core.controller.dto.mapper.DtoMappingConditions.*;

@WebController
public class OwnerController extends CrudController<Owner, Long> {


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {

            builder.when(endpoint(getCreateUrl()))
                    .thenReturn(CreateOwnerDto.class);

            builder.when(direction(Direction.RESPONSE).and(principal(Principal.OWN)))
                    .thenReturn(ReadOwnOwnerDto.class);

            builder.when(direction(Direction.RESPONSE).and(principal(Principal.FOREIGN)))
                    .thenReturn(ReadForeignOwnerDto.class);

            builder.when(any())
                    .thenReturn(DefaultDto.class);

            builder.when(direction(Direction.RESPONSE))
                    .thenReturn(DefaultResponseDto.class);

            builder.when(direction(Direction.RESPONSE).and(notAuthenticated()))
                    .thenThrow(new AccessDeniedException(""));
        };
    };

//    @Override
//    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
//        return builder
//
//                .forEndpoint(getCreateUrl(),CreateOwnerDto.class)
//                .forUpdate(UpdateOwnerDto.class)
//                //response dto config
//                //authenticated
//                .withPrincipal(DtoRequestInfo.Principal.OWN)
//                .forResponse(ReadOwnOwnerDto.class)
//                .withPrincipal(DtoRequestInfo.Principal.FOREIGN)
//                .forResponse(ReadForeignOwnerDto.class)
//                //not authenticated
//                .withAllPrincipals()
//                .forResponse(ReadForeignOwnerDto.class)
//
//                // if you can update or create you can read all of the data
//                .forEndpoint(getCreateUrl(),Direction.RESPONSE,ReadOwnOwnerDto.class)
//                .forEndpoint(getUpdateUrl(),Direction.RESPONSE,ReadOwnOwnerDto.class)
//
//                .build();
//    }


    @Autowired
    public void configureAllowedFilters(CityPrefixFilter cityPrefixFilter, HasPetsFilter hasPetsFilter, OwnerTelNumberFilter ownerTelNumberFilter, PetNameEndsWithFilter parentFilter) {
        addAllowedExtensions(cityPrefixFilter,hasPetsFilter,ownerTelNumberFilter,parentFilter);
    }

    @Autowired
    public void configureAllowedSorting(LastNameAscSorting lastNameAscSorting, LastNameDescSorting lastNameDescSorting) {
        addAllowedExtensions(lastNameAscSorting, lastNameDescSorting);
    }

}
