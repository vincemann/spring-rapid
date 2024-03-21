package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.syncdemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Validated
public interface PetService {

    Optional<Pet> findByName(String name);
    Pet create(@Valid CreatePetDto dto) throws EntityNotFoundException, BadEntityException;

    List<Pet> findAllById(List<Long> ids);
}
