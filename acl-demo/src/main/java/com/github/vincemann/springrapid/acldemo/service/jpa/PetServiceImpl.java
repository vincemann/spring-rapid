package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.controller.map.PetMappingService;
import com.github.vincemann.springrapid.acldemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.OwnerUpdatesPetDto;
import com.github.vincemann.springrapid.acldemo.dto.pet.UpdateIllnessDto;
import com.github.vincemann.springrapid.acldemo.model.Illness;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.repo.IllnessRepository;
import com.github.vincemann.springrapid.acldemo.repo.PetRepository;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.auth.ex.BadEntityException;
import com.github.vincemann.springrapid.auth.ex.EntityNotFoundException;
import com.github.vincemann.springrapid.auth.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.Optional;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;
import static com.github.vincemann.springrapid.core.util.ValidationUtils.validate;


@Primary
@Service
public class PetServiceImpl implements PetService {

    private RapidAclService aclService;

    private PetRepository repository;

    private IllnessRepository illnessRepository;

    private Validator validator;

    @Autowired
    private PetMappingService mappingService;



    @Transactional
    @Override
    public Pet create(CreatePetDto dto) throws EntityNotFoundException {
        Pet pet = mappingService.map(dto);
        Pet saved = repository.save(pet);
        saveAclInfo(saved);
        return pet;
    }


    @Transactional
    @Override
    public Pet addIllnesses(UpdateIllnessDto dto) throws EntityNotFoundException, BadEntityException {
        Pet pet = findPresentById(repository, dto.getId());
        Optional<Illness> illness = illnessRepository.findByName(dto.getIllnessName());
        VerifyEntity.isPresent(illness,dto.getIllnessName(), Illness.class);
        boolean hasIllness = pet.getIllnesses().stream().anyMatch(i -> i.getName().equals(dto.getIllnessName()));
        VerifyEntity.isTrue(!hasIllness,"pet already has illness to add");
        pet.addIllness(illness.get());
        return pet;
    }

    @Transactional
    @Override
    public Pet removeIllness(UpdateIllnessDto dto) throws EntityNotFoundException, BadEntityException {
        Pet pet = findPresentById(repository, dto.getId());
        Optional<Illness> illness = illnessRepository.findByName(dto.getIllnessName());
        boolean hasIllness = pet.getIllnesses().stream().anyMatch(i -> i.getName().equals(dto.getIllnessName()));
        VerifyEntity.isTrue(hasIllness,"pet does not have illness to remove");
        pet.removeIllness(illness.get());
        return pet;
    }

    @Transactional
    @Override
    public Pet ownerUpdatesPet(OwnerUpdatesPetDto dto) throws EntityNotFoundException, BadEntityException {
        Pet pet = findPresentById(repository, dto.getId());
        if (dto.getName() != null){
            validate(validator,dto,dto::getName);
            Optional<Pet> duplicate = repository.findByName(dto.getName());
            if (duplicate.isPresent())
                throw new BadEntityException("A pet with that name already exists");
            pet.setName(dto.getName());
        }
        if (dto.getBirthDate() != null){
            validate(validator,dto,dto::getBirthDate);
            pet.setBirthDate(dto.getBirthDate());
        }
        return pet;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Pet> findByName(String name) {
        return repository.findByName(name);
    }


    private void saveAclInfo(Pet pet) {
        // authenticated gains admin permission
        aclService.grantAuthenticatedPermissionForEntity(pet, BasePermission.ADMINISTRATION);
        // owner of pet gains admin permission
        aclService.grantUserPermissionForEntity(pet.getOwner().getContactInformation(), pet, BasePermission.ADMINISTRATION);
        // vets can read & write
        aclService.grantRolePermissionForEntity(Roles.VET, pet, BasePermission.READ, BasePermission.WRITE);
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
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
    public void setAclService(RapidAclService aclService) {
        this.aclService = aclService;
    }
}
