package com.github.vincemann.springrapid.acldemo.controller.map;

import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.dto.visit.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.repo.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.repo.PetRepository;
import com.github.vincemann.springrapid.acldemo.repo.VetRepository;
import com.github.vincemann.springrapid.auth.ex.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.stream.Collectors;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;

@Service
public class VisitMappingService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private VetRepository vetRepository;
    @Autowired
    private PetRepository petRepository;

    @Transactional
    public ReadVisitDto map(Visit visit){
        ReadVisitDto dto = new ModelMapper().map(visit, ReadVisitDto.class);
        dto.setOwnerId(visit.getOwner() == null ? null : visit.getOwner().getId());
        dto.setVetId(visit.getVet() == null ? null : visit.getVet().getId());
        dto.setPetIds(visit.getPets().stream().map(Pet::getId).collect(Collectors.toSet()));
        return dto;
    }

    @Transactional
    public Visit map(CreateVisitDto dto) throws EntityNotFoundException {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Owner owner = findPresentById(ownerRepository, dto.getOwnerId());
        Vet vet = findPresentById(vetRepository, dto.getVetId());
        Visit visit = modelMapper.map(dto, Visit.class);
        visit = entityManager.merge(visit);

        for (Long petId : dto.getPetIds()) {
            Pet pet = findPresentById(petRepository, petId);
            visit.addPet(pet);
        }
        visit.setOwner(owner);
        visit.setVet(vet);
        return visit;
    }
}
