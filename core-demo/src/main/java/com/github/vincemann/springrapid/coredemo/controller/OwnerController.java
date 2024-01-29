package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import com.github.vincemann.springrapid.coredemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.coredemo.service.filter.CityPrefixFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.HasPetsFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.OwnerTelNumberFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.PetNameEndsWithFilter;
import com.github.vincemann.springrapid.coredemo.service.sort.LastNameAscSorting;
import com.github.vincemann.springrapid.coredemo.service.sort.LastNameDescSorting;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

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
    }



    @Autowired
    public void configureAllowedFilters(CityPrefixFilter cityPrefixFilter,
                                        HasPetsFilter hasPetsFilter,
                                        OwnerTelNumberFilter ownerTelNumberFilter,
                                        PetNameEndsWithFilter parentFilter) {
        registerExtensions(cityPrefixFilter, hasPetsFilter, ownerTelNumberFilter, parentFilter);
    }

    @Autowired
    public void configureAllowedSorting(LastNameAscSorting lastNameAscSorting,
                                        LastNameDescSorting lastNameDescSorting) {
        registerExtensions(lastNameAscSorting, lastNameDescSorting);
    }

}
