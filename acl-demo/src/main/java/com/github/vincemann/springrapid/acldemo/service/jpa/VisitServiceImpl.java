package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.dto.visit.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.acldemo.repo.*;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;


@Primary
@Service
public class VisitServiceImpl implements VisitService {


    private RapidAclService aclService;
    private UserRepository userRepository;

    private VisitRepository visitRepository;

    private OwnerRepository ownerRepository;

    private VetRepository vetRepository;
    private PetRepository petRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    @Override
    public void addSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException {
        User spectator = findPresentById(userRepository,spectatorId);
        Visit visit = findPresentById(visitRepository,visitId);
        aclService.grantUserPermissionForEntity(spectator.getContactInformation(),visit,BasePermission.READ);
    }

    @Transactional
    @Override
    public void removeSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException {
        User spectator = findPresentById(userRepository,spectatorId);
        Visit visit = findPresentById(visitRepository,visitId);
        aclService.revokeRolesPermissionForEntity(spectator.getContactInformation(),visit,true,BasePermission.READ);
    }

    @Transactional
    @Override
    public Visit create(CreateVisitDto dto) throws BadEntityException, EntityNotFoundException {
        Visit visit = map(dto);
        Visit saved = visitRepository.save(visit);
        saveAclInfo(saved);
        return saved;
    }

    Visit map(CreateVisitDto dto) throws EntityNotFoundException {
        Visit visit = new ModelMapper().map(dto, Visit.class);
        visit = entityManager.merge(visit);
        Owner owner = findPresentById(ownerRepository, dto.getOwnerId());
        Vet vet = findPresentById(vetRepository, dto.getVetId());
        for (Long petId : dto.getPetIds()) {
            Pet pet = findPresentById(petRepository, petId);
            visit.addPet(pet);
        }
        visit.setOwner(owner);
        visit.setVet(vet);
        return visit;
    }

    private void saveAclInfo(Visit visit) {
        // participating vet gets admin permission for visit
        aclService.grantUserPermissionForEntity(visit.getVet().getContactInformation(), visit, BasePermission.ADMINISTRATION);
        // participating owner can read
        aclService.grantUserPermissionForEntity(visit.getOwner().getContactInformation(), visit, BasePermission.READ);
        // all vets can read all visits to plan properly
        aclService.grantRolePermissionForEntity(Roles.VET,visit,BasePermission.READ);
    }

    @Autowired
    public void setPetRepository(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Autowired
    public void setAclPermissionService(RapidAclService rapidAclService) {
        this.aclService = rapidAclService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setVisitRepository(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Autowired
    public void setOwnerRepository(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Autowired
    public void setVetRepository(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }
}

