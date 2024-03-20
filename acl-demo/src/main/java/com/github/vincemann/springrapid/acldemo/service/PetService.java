package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.core.service.CrudService;

import java.util.Optional;


public interface PetService extends CrudService<Pet,Long, CreatePetDto> {

    Optional<Pet> findByName(String name);

}
