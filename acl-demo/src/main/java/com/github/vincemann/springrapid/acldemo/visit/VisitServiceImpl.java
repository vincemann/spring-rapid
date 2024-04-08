package com.github.vincemann.springrapid.acldemo.visit;

import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acldemo.Roles;
import com.github.vincemann.springrapid.acldemo.visit.dto.CreateVisitDto;
import com.github.vincemann.springrapid.acldemo.user.User;
import com.github.vincemann.springrapid.acldemo.user.UserRepository;
import com.github.vincemann.springrapid.auth.BadEntityException;
import com.github.vincemann.springrapid.auth.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.github.vincemann.springrapid.core.util.RepositoryUtil.findPresentById;


@Primary
@Service
public class VisitServiceImpl implements VisitService {


    private RapidAclService aclService;
    private UserRepository userRepository;

    private VisitRepository repository;

    @Autowired
    private VisitMappingService mappingService;


    @Transactional
    @Override
    public void addSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException {
        User spectator = findPresentById(userRepository,spectatorId);
        Visit visit = findPresentById(repository,visitId);
        aclService.grantUserPermissionForEntity(spectator.getContactInformation(),visit,BasePermission.READ);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Visit> find(long id) {
        return repository.findById(id);
    }

    @Transactional
    @Override
    public void removeSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException {
        User spectator = findPresentById(userRepository,spectatorId);
        Visit visit = findPresentById(repository,visitId);
        aclService.revokeUsersPermissionForEntity(spectator.getContactInformation(),visit,true,BasePermission.READ);
    }

    @Transactional
    @Override
    public Visit create(CreateVisitDto dto) throws BadEntityException, EntityNotFoundException {
        Visit visit = mappingService.map(dto);
        Visit saved = repository.save(visit);
        saveAclInfo(saved);
        return saved;
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
    public void setAclPermissionService(RapidAclService rapidAclService) {
        this.aclService = rapidAclService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRepository(VisitRepository repository) {
        this.repository = repository;
    }

}

