package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.syncdemo.dto.pet.CreatePetDto;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.model.PetType;
import com.github.vincemann.springrapid.syncdemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.syncdemo.repo.PetRepository;
import com.github.vincemann.springrapid.syncdemo.repo.PetTypeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;


@Primary
@Service
public class PetServiceImpl implements PetService {

    private OwnerRepository ownerRepository;
    private PetTypeRepository petTypeRepository;
    private PetRepository repository;

    @Transactional
    @Override
    public Pet create(CreatePetDto dto) throws EntityNotFoundException {
        Pet pet = map(dto);
        return repository.save(pet);
    }

    Pet map(CreatePetDto dto) throws EntityNotFoundException {
        Owner owner = findPresentById(ownerRepository, dto.getOwnerId());
        PetType petType = findPresentById(petTypeRepository, dto.getPetTypeId());
        Pet pet = new ModelMapper().map(dto, Pet.class);
        owner.addPet(pet);
        pet.setPetType(petType);
        return pet;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Pet> findAllById(List<Long> ids) {
        return repository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Pet> findByName(String name) {
        return repository.findByName(name);
    }

    @Autowired
    public void setRepository(PetRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setOwnerRepository(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Autowired
    public void setPetTypeRepository(PetTypeRepository petTypeRepository) {
        this.petTypeRepository = petTypeRepository;
    }
}
