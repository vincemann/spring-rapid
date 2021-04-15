package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.coredemo.dtos.pet.PetDto;
import com.github.vincemann.springrapid.coredemo.dtos.pet.UpdatePetDto;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.service.PetService;


@WebController
public class PetController extends CrudController<Pet, Long, PetService> {

    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder
                .forAll(PetDto.class)
                .forEndpoint(getUpdateUrl(), Direction.REQUEST, UpdatePetDto.class)
                .build();
    }
}
