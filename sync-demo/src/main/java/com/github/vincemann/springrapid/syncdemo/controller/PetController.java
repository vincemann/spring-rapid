package com.github.vincemann.springrapid.syncdemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.syncdemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.ReadPetDto;
import com.github.vincemann.springrapid.syncdemo.dto.pet.UpdatePetDto;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.service.PetService;
import org.springframework.stereotype.Controller;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;


@Controller
public class PetController extends CrudController<Pet, Long, PetService> {


    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {
        builder.when(endpoint(getUpdateUrl()).and(direction(Direction.REQUEST)))
                .thenReturn(UpdatePetDto.class);

        builder.when(endpoint(getCreateUrl()).and(direction(Direction.REQUEST)))
                .thenReturn(CreatePetDto.class);

        builder.when(direction(Direction.RESPONSE)).thenReturn(ReadPetDto.class);
    }

}
