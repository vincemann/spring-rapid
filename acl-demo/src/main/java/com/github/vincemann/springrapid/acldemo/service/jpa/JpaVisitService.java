package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.model.abs.User;
import com.github.vincemann.springrapid.acldemo.repo.VisitRepository;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.acldemo.service.user.MyUserService;
import com.github.vincemann.springrapid.core.Root;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Primary
@Service
public class JpaVisitService
        extends JpaCrudService<Visit, Long, VisitRepository>
        implements VisitService {


    private RapidAclService aclService;
    private MyUserService userService;

    @Transactional
    @Override
    public void addSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException {
        User spectator = userService.findPresentById(spectatorId);
        Visit visit = findPresentById(visitId);
        aclService.grantUserPermissionForEntity(spectator.getContactInformation(),visit,BasePermission.READ);
    }

    @Transactional
    @Override
    public void removeSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException {
        User spectator = userService.findPresentById(spectatorId);
        Visit visit = findPresentById(visitId);
        aclService.revokeRolesPermissionForEntity(spectator.getContactInformation(),visit,true,BasePermission.READ);
    }

    @Transactional
    @Override
    public Visit create(Visit entity) throws BadEntityException {
        Visit visit = super.create(entity);
        saveAclInfo(visit);
        return visit;
    }

    private void saveAclInfo(Visit visit) {
        // participating vet gets admin permission for visit
        aclService.grantUserPermissionForEntity(visit.getVet().getContactInformation(), visit, BasePermission.ADMINISTRATION);
        // participating owner can read
        aclService.grantUserPermissionForEntity(visit.getOwner().getContactInformation(), visit, BasePermission.READ);
    }

    @Autowired
    public void setAclPermissionService(RapidAclService rapidAclService) {
        this.aclService = rapidAclService;
    }

    @Autowired
    @Root
    public void setUserService(MyUserService userService) {
        this.userService = userService;
    }

}

