package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.UpdatePetsIllnessesDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;

import java.util.Optional;


public interface PetService {

    Optional<Pet> findByName(String name);
    Pet create(CreatePetDto dto) throws EntityNotFoundException;
    Pet updateIllnesses(UpdatePetsIllnessesDto dto) throws EntityNotFoundException;

    void updateName(String oldName, String name) throws EntityNotFoundException, BadEntityException;
}
