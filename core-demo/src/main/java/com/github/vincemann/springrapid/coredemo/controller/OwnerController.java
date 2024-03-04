package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoRequestInfo;
import com.github.vincemann.springrapid.core.controller.dto.map.Principal;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.coredemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadForeignOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.ReadOwnOwnerDto;
import com.github.vincemann.springrapid.coredemo.dto.owner.UpdateOwnerDto;
import com.github.vincemann.springrapid.coredemo.model.Owner;
import com.github.vincemann.springrapid.coredemo.service.OwnerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.github.vincemann.springrapid.coredemo.service.filter.CityPrefixFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.HasPetsFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.OwnerTelNumberFilter;
import com.github.vincemann.springrapid.coredemo.service.filter.PetNameEndsWithFilter;
import com.github.vincemann.springrapid.coredemo.service.sort.LastNameAscSorting;
import com.github.vincemann.springrapid.coredemo.service.sort.LastNameDescSorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.function.Predicate;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;

@Controller
public class OwnerController extends CrudController<Owner, Long, OwnerService> {


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {

        builder.when(endpoint(getCreateUrl()).and(direction(Direction.REQUEST)))
                .thenReturn(CreateOwnerDto.class);

        builder.when(endpoint(getUpdateUrl()).and(direction(Direction.REQUEST)))
                .thenReturn(UpdateOwnerDto.class);

        builder.when(updateOrCreateResponse())
                .thenReturn(ReadOwnOwnerDto.class);

        builder.when(direction(Direction.RESPONSE).and(principal(Principal.OWN)))
                .thenReturn(ReadOwnOwnerDto.class);

        builder.when(direction(Direction.RESPONSE).and(principal(Principal.FOREIGN)))
                .thenReturn(ReadForeignOwnerDto.class);


        // anon user
        builder.when(direction(Direction.RESPONSE))
                .thenReturn(ReadForeignOwnerDto.class);
    }

    // showcase for using own predicate combinations
    private Predicate<DtoRequestInfo> updateOrCreateResponse(){
        Predicate<DtoRequestInfo> updateOrCreate = endpoint(getUpdateUrl()).or(endpoint(getCreateUrl()));
        return updateOrCreate.and(direction(Direction.RESPONSE));
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
