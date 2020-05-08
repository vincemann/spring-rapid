package io.github.vincemann.springrapid.demo.controllers;

import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.demo.dtos.pet.BasePetDto;
import io.github.vincemann.springrapid.demo.dtos.pet.UpdatePetDto;
import io.github.vincemann.springrapid.demo.model.Pet;
import io.github.vincemann.springrapid.core.slicing.components.WebController;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.Direction;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;
import io.github.vincemann.springrapid.demo.service.PetService;


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
