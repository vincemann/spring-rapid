package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappings;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.syncdemo.dto.pet.PetDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.UpdatePetDto;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.PetService;


@WebController
public class PetController extends CrudController<Pet, Long, PetService> {

    @Override
    protected DtoMappings provideDtoMappingContext(CrudDtoMappingContextBuilder builder) {
        return builder
                .forAll(PetDto.class)
                .forEndpoint(getUpdateUrl(), Direction.REQUEST, UpdatePetDto.class)
                .build();
    }
}
