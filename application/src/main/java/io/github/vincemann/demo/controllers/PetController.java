package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.pet.BasePetDto;
import io.github.vincemann.demo.dtos.pet.UpdatePetDto;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.generic.crud.lib.config.layers.component.ControllerComponent;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterJsonDtoCrudController;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;


@ControllerComponent
public class PetController
        extends SpringAdapterJsonDtoCrudController<Pet, Long> {

    public PetController() {
        super(DtoMappingContext.DEFAULT(BasePetDto.class));
        getDtoMappingContext().setPartialUpdateRequestDtoClass(UpdatePetDto.class);
    }
}
