package com.github.vincemann.springrapid.demo.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.demo.dtos.pet.BasePetDto;
import com.github.vincemann.springrapid.demo.dtos.pet.UpdatePetDto;
import com.github.vincemann.springrapid.demo.model.Pet;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.RapidDtoEndpoint;
import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.demo.service.PetService;


@WebController
public class PetController extends CrudController<Pet, Long, PetService> {

    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return DtoMappingContextBuilder.builder()
                .forAll(BasePetDto.class)
                .forEndpoint(properties.controller.endpoints.update, Direction.REQUEST, UpdatePetDto.class)
                .build();
    }
}
