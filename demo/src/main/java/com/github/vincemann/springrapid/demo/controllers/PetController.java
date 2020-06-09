package com.github.vincemann.springrapid.demo.controllers;

import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.demo.dtos.pet.BasePetDto;
import com.github.vincemann.springrapid.demo.dtos.pet.UpdatePetDto;
import com.github.vincemann.springrapid.demo.model.Pet;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import com.github.vincemann.springrapid.core.controller.rapid.RapidController;
import com.github.vincemann.springrapid.demo.service.PetService;


@WebController
public class PetController extends RapidController<Pet, Long, PetService> {

    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return DtoMappingContextBuilder.builder()
                .forAll(BasePetDto.class)
                .forEndpoint(RapidDtoEndpoint.UPDATE, Direction.REQUEST, UpdatePetDto.class)
                .build();
    }
}
