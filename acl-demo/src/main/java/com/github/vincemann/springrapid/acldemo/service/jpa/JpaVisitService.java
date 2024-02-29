package com.github.vincemann.springrapid.acldemo.service.jpa;

import com.github.vincemann.springrapid.acl.service.RapidAclService;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.repo.VisitRepository;
import com.github.vincemann.springrapid.acldemo.service.OwnerService;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.service.JpaCrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Primary
@Service
public class JpaVisitService
        extends JpaCrudService<Visit, Long, VisitRepository>
        implements VisitService {


    private RapidAclService aclService;


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

    @Override
    public Class<?> getTargetClass() {
        return JpaVisitService.class;
    }
}

