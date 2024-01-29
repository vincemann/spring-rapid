package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.DtoMappings;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.coredemo.dto.pet.PetDto;
import com.github.vincemann.springrapid.coredemo.dto.pet.UpdatePetDto;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.service.PetService;
import com.github.vincemann.springrapid.coredemo.service.filter.*;
import org.springframework.beans.factory.annotation.Autowired;


@WebController
public class PetController extends CrudController<Pet, Long, PetService> {

    @Override
    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder
                .forAll(PetDto.class)
                .forEndpoint(getUpdateUrl(), Direction.REQUEST, UpdatePetDto.class)
                .build();
    }


    @Autowired
    public void configureAllowedExtensions(PetsParentFilter petsOfOwnerFilter) {
        addAllowedExtensions(petsOfOwnerFilter);
    }
}
