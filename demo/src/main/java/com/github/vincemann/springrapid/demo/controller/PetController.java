package com.github.vincemann.springrapid.demo.controller;

import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.demo.dtos.pet.BasePetDto;
import com.github.vincemann.springrapid.demo.dtos.pet.UpdatePetDto;
import com.github.vincemann.springrapid.demo.model.Pet;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.RapidDtoEndpoint;
import com.github.vincemann.springrapid.core.controller.GenericCrudController;
import com.github.vincemann.springrapid.demo.service.PetService;


@WebController
public class PetController extends GenericCrudController<Pet, Long, PetService> {

    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return CrudDtoMappingContextBuilder.builder()
                .forAll(BasePetDto.class)
                .forEndpoint(properties.controller.endpoints.update, Direction.REQUEST, UpdatePetDto.class)
                .build();
    }
}
