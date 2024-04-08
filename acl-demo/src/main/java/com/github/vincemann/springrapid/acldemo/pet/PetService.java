package com.github.vincemann.springrapid.acldemo.pet;

import com.github.vincemann.springrapid.acldemo.pet.dto.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.pet.dto.OwnerUpdatesPetDto;
import com.github.vincemann.springrapid.acldemo.pet.dto.UpdateIllnessDto;
import com.github.vincemann.springrapid.acldemo.pet.Pet;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.Optional;



@Validated
public interface PetService {

    Optional<Pet> findByName(String name);
    Pet create(@Valid CreatePetDto dto) throws EntityNotFoundException, BadEntityException;
    Pet addIllnesses(@Valid UpdateIllnessDto dto) throws EntityNotFoundException, BadEntityException;

    @Transactional
    Pet removeIllness(@Valid UpdateIllnessDto dto) throws EntityNotFoundException, BadEntityException;

    Pet ownerUpdatesPet(OwnerUpdatesPetDto dto) throws EntityNotFoundException, BadEntityException;
}
