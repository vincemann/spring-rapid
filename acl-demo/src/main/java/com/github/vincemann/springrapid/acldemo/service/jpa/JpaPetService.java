package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.UpdatePetsIllnessesDto;
import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.PetType;
import com.github.vincemann.springrapid.acldemo.repo.IllnessRepository;
import com.github.vincemann.springrapid.acldemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.repo.PetRepository;
import com.github.vincemann.springrapid.acldemo.repo.PetTypeRepository;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;


@Primary
@Service
public class JpaPetService implements PetService {

    private RapidAclService aclService;
    private OwnerRepository ownerRepository;
    private PetTypeRepository petTypeRepository;

    private PetRepository repository;

    private IllnessRepository illnessRepository;


    @Transactional
    @Override
    public Pet create(CreatePetDto dto) throws EntityNotFoundException {
        Pet pet = map(dto);
        Pet saved = repository.save(pet);
        saveAclInfo(saved);
        return pet;
    }

    Pet map(CreatePetDto dto) throws EntityNotFoundException {
        Owner owner = findPresentById(ownerRepository, dto.getOwnerId());
        PetType petType = findPresentById(petTypeRepository, dto.getPetTypeId());
        Pet pet = new ModelMapper().map(dto, Pet.class);
        owner.addPet(pet);
        pet.setPetType(petType);
        return pet;
    }

    @Transactional
    @Override
    public Pet updateIllnesses(UpdatePetsIllnessesDto dto) throws EntityNotFoundException {
        Pet pet = findPresentById(repository, dto.getId());
        Set<Illness> illnesses = new HashSet<>();
        for (Long id : dto.getIllnessIds()) {
            Optional<Illness> illness = illnessRepository.findById(id);
            VerifyEntity.isPresent(illness,id, Illness.class);
            illnesses.add(illness.get());
        }
        pet.setIllnesss(illnesses);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Pet> findByName(String name) {
        return repository.findByName(name);
    }

    @Transactional
    @Override
    public void updateName(String oldName, String name) throws EntityNotFoundException, BadEntityException {
        Optional<Pet> pet = repository.findByName(oldName);
        VerifyEntity.isPresent(pet,name,Pet.class);
        Optional<Pet> duplicate = repository.findByName(name);
        if (duplicate.isPresent())
            throw new BadEntityException("A pet with that name already exists");
        pet.get().setName(name);
    }

    private void saveAclInfo(Pet pet){
        // authenticated gains admin permission
        aclService.grantAuthenticatedPermissionForEntity(pet, BasePermission.ADMINISTRATION);
        // owner of pet gains admin permission
        aclService.grantUserPermissionForEntity(pet.getOwner().getContactInformation(), pet, BasePermission.ADMINISTRATION);
        // vets can read & write
        aclService.grantRolePermissionForEntity(Roles.VET, pet, BasePermission.READ, BasePermission.WRITE);
    }

    @Autowired
    public void setRepository(PetRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setIllnessRepository(IllnessRepository illnessRepository) {
        this.illnessRepository = illnessRepository;
    }

    @Autowired
    public void setOwnerRepository(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Autowired
    public void setPetTypeRepository(PetTypeRepository petTypeRepository) {
        this.petTypeRepository = petTypeRepository;
    }

    @Autowired
    public void setAclService(RapidAclService aclService) {
        this.aclService = aclService;
    }
}
