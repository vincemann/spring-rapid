package com.github.vincemann.springrapid.acldemo.service;

import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerUpdatesPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.VetUpdatesPetDto;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Optional;



@Validated
public interface PetService {

    Optional<Pet> findByName(String name);
    Pet create(@Valid CreatePetDto dto) throws EntityNotFoundException, BadEntityException;
    Pet vetUpdatesPet(VetUpdatesPetDto dto) throws EntityNotFoundException;
    Pet ownerUpdatesPet(OwnerUpdatesPetDto dto) throws EntityNotFoundException, BadEntityException;
}
