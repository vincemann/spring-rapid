package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.CrudDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.Direction;
import com.github.vincemann.springrapid.core.controller.dto.mapper.context.DtoMappingContext;
import com.github.vincemann.springrapid.core.controller.parentaware.ParentAwareCrudController;
import com.github.vincemann.springrapid.core.controller.parentaware.ParentAwareDtoMappingContextBuilder;
import com.github.vincemann.springrapid.core.slicing.WebController;
import com.github.vincemann.springrapid.syncdemo.dto.pet.PetDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.UpdatePetDto;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.PetService;


@WebController
public class PetController extends ParentAwareCrudController<Pet, Long,Long, PetService> {

    @Override
    protected DtoMappingContext provideDtoMappingContext(ParentAwareDtoMappingContextBuilder builder) {
        return builder
                .forAll(PetDto.class)
                .forEndpoint(getUpdateUrl(), Direction.REQUEST, UpdatePetDto.class)
                .build();
    }
}
