package com.github.vincemann.springrapid.syncdemo.controller.map;

import com.github.vincemann.springrapid.syncdemo.dto.pet.ReadPetDto;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.model.Toy;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetMappingService {

    @Transactional
    public ReadPetDto map(Pet pet){
        ReadPetDto dto = new ModelMapper().map(pet, ReadPetDto.class);
        dto.setPetTypeId(pet.getPetType().getId());
        dto.setOwnerId(pet.getOwner().getId());
        dto.setToyIds(pet.getToys().stream().map(Toy::getId).collect(Collectors.toSet()));
        return dto;
    }

    @Transactional
    public List<ReadPetDto> map(List<Pet> pets){
        return pets.stream().map(this::map)
                .collect(Collectors.toList());
    }
}
