package com.github.vincemann.springrapid.acldemo.visit;

import com.github.vincemann.springrapid.acldemo.visit.dto.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.visit.dto.ReadVisitDto;
import com.github.vincemann.springrapid.acldemo.owner.Owner;
import com.github.vincemann.springrapid.acldemo.pet.Pet;
import com.github.vincemann.springrapid.acldemo.vet.Vet;
import com.github.vincemann.springrapid.acldemo.owner.OwnerRepository;
import com.github.vincemann.springrapid.acldemo.pet.PetRepository;
import com.github.vincemann.springrapid.acldemo.vet.VetRepository;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
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
