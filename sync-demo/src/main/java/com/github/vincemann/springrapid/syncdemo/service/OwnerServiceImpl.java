package com.github.vincemann.springrapid.syncdemo.service;

import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.RepositoryUtil;
import com.github.vincemann.springrapid.syncdemo.dto.owner.CreateOwnerDto;
import com.github.vincemann.springrapid.syncdemo.model.ClinicCard;
import com.github.vincemann.springrapid.syncdemo.model.Owner;
import com.github.vincemann.springrapid.syncdemo.model.Pet;
import com.github.vincemann.springrapid.syncdemo.repo.ClinicCardRepository;
import com.github.vincemann.springrapid.syncdemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.syncdemo.repo.PetRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;

@Service
public class OwnerServiceImpl implements OwnerService {

    @Autowired
    private OwnerRepository repository;
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ClinicCardRepository clinicCardRepository;

    @Transactional
    @Override
    public Owner create(CreateOwnerDto dto) throws EntityNotFoundException {
        Owner owner = map(dto);
        return repository.save(owner);
    }

    Owner map(CreateOwnerDto dto) throws EntityNotFoundException {
        Owner owner = new ModelMapper().map(dto, Owner.class);
        if (dto.getClinicCardId() != null){
            ClinicCard card = findPresentById(clinicCardRepository, dto.getClinicCardId());
            owner.addClinicCard(card);
        }
        if (dto.getPetIds() != null){
            for (Long petId : dto.getPetIds()) {
                Pet pet = findPresentById(petRepository, petId);
                owner.addPet(pet);
            }
        }
        return owner;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Owner> find(long id) {
        return repository.findById(id);
    }
}
