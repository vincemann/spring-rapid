package com.github.vincemann.springrapid.acldemo.controller.map;

import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerReadsForeignPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerReadsOwnPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.VetReadsPetDto;
import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class PetMappingService {


    @Transactional
    public Object mapToReadPetDto(Pet pet){
        if (RapidSecurityContext.getRoles().contains(Roles.OWNER)){
            if (pet.getOwner().getContactInformation().equals(RapidSecurityContext.getName())){
                return mapToOwnerReadsOwnPetDto(pet);
            }
            else{
                return mapToOwnerReadsForeignPetDto(pet);
            }
        }
        else{
            return mapToVetReadsPetDto(pet);
        }
    }

    @Transactional
    public VetReadsPetDto mapToVetReadsPetDto(Pet pet){
        VetReadsPetDto dto = new ModelMapper().map(pet, VetReadsPetDto.class);
        dto.setOwnerId(pet.getOwner() == null ? null : pet.getOwner().getId());
        dto.setPetTypeId(pet.getPetType() == null ? null : pet.getPetType().getId());
        dto.setIllnessIds(pet.getIllnesss().stream().map(Illness::getId).collect(Collectors.toSet()));
        return dto;
    }


    @Transactional
    public OwnerReadsForeignPetDto mapToOwnerReadsForeignPetDto(Pet pet){
        OwnerReadsForeignPetDto dto = new ModelMapper().map(pet, OwnerReadsForeignPetDto.class);
        dto.setOwnerId(pet.getOwner() == null ? null : pet.getOwner().getId());
        dto.setPetTypeId(pet.getPetType() == null ? null : pet.getPetType().getId());
        return dto;
    }

    @Transactional
    public OwnerReadsOwnPetDto mapToOwnerReadsOwnPetDto(Pet pet){
        OwnerReadsOwnPetDto dto = new ModelMapper().map(pet, OwnerReadsOwnPetDto.class);
        dto.setOwnerId(pet.getOwner() == null ? null : pet.getOwner().getId());
        dto.setPetTypeId(pet.getPetType() == null ? null : pet.getPetType().getId());
        dto.setIllnessIds(pet.getIllnesss().stream().map(Illness::getId).collect(Collectors.toSet()));
        return dto;
    }
}
