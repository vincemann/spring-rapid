package com.github.vincemann.springrapid.coredemo.controller;

import com.github.vincemann.springrapid.core.controller.CrudController;
import com.github.vincemann.springrapid.core.controller.dto.map.Direction;
import com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingsBuilder;
import com.github.vincemann.springrapid.coredemo.dto.pet.UpdatePetDto;
import org.springframework.stereotype.Controller;
import com.github.vincemann.springrapid.coredemo.dto.ClinicCardDto;
import com.github.vincemann.springrapid.coredemo.dto.pet.PetDto;
import com.github.vincemann.springrapid.coredemo.model.Pet;
import com.github.vincemann.springrapid.coredemo.service.filter.PetsParentFilter;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.vincemann.springrapid.core.controller.dto.map.DtoMappingConditions.*;


@Controller
public class PetController extends CrudController<Pet, Long> {

    @Override
    protected void configureDtoMappings(DtoMappingsBuilder builder) {

        builder.when(endpoint(getUpdateUrl()).and(direction(Direction.REQUEST)))
                .thenReturn(UpdatePetDto.class);

        builder.when(any())
                .thenReturn(PetDto.class);
    }


    @Autowired
    public void configureAllowedExtensions(PetsParentFilter petsOfOwnerFilter) {
        registerExtensions(petsOfOwnerFilter);
    }
}
