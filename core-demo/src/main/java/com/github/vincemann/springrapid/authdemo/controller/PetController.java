package com.github.vincemann.springrapid.authdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.slicing.components.WebController;
import com.github.vincemann.springrapid.authdemo.dtos.pet.BasePetDto;
import com.github.vincemann.springrapid.authdemo.dtos.pet.UpdatePetDto;
import com.github.vincemann.springrapid.authdemo.model.Pet;
import com.github.vincemann.springrapid.authdemo.service.PetService;


@WebController
public class PetController extends CrudController<Pet, Long, PetService> {

    @Override
    protected DtoMappingContext provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder
                .forAll(BasePetDto.class)
                .forEndpoint(getUpdateUrl(), Direction.REQUEST, UpdatePetDto.class)
                .build();
    }
}
