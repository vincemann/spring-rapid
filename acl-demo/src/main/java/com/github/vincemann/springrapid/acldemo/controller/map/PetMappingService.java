package com.github.vincemann.springrapid.acldemo.controller.map;

import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerReadsForeignPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerReadsOwnPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.VetReadsPetDto;
import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.PetType;
import com.github.vincemann.springrapid.acldemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.repo.PetRepository;
import com.github.vincemann.springrapid.acldemo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.stream.Collectors;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;

@Service
public class PetMappingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetTypeRepository petTypeRepository;

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

    public Pet map(CreatePetDto dto) throws EntityNotFoundException {
        Pet pet = new ModelMapper().map(dto, Pet.class);
        pet = entityManager.merge(pet);
        Owner owner = findPresentById(ownerRepository, dto.getOwnerId());
        PetType petType = findPresentById(petTypeRepository, dto.getPetTypeId());
        owner.addPet(pet);
        pet.setPetType(petType);
        return pet;
    }

    @Transactional
    public VetReadsPetDto mapToVetReadsPetDto(Pet pet){
        VetReadsPetDto dto = new ModelMapper().map(pet, VetReadsPetDto.class);
        dto.setOwnerId(pet.getOwner() == null ? null : pet.getOwner().getId());
        dto.setPetTypeId(pet.getPetType() == null ? null : pet.getPetType().getId());
        dto.setIllnessIds(pet.getIllnesses().stream().map(Illness::getId).collect(Collectors.toSet()));
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
        dto.setIllnessIds(pet.getIllnesses().stream().map(Illness::getId).collect(Collectors.toSet()));
        return dto;
    }
}
