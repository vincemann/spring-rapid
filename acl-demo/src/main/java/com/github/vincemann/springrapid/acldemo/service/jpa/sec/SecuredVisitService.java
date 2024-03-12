package com.github.vincemann.springrapid.acldemo.service.jpa.sec;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acl.service.SecuredCrudServiceDecorator;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.auth.model.AuthRoles;
import com.github.vincemann.springrapid.auth.service.UserService;
import com.github.vincemann.springrapid.core.sec.AuthorizationTemplate;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyAccess;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Secured
@Service
public class SecuredVisitService
        extends SecuredCrudServiceDecorator<VisitService, Visit,Long>
    implements VisitService
{

    @Autowired
    public SecuredVisitService(VisitService decorated) {
        super(decorated);
    }


    @Transactional
    @PreAuthorize("hasPermission(#visitId, 'com.github.vincemann.springrapid.acldemo.model.Visit', 'administration')")
    @Override
    public void addSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException {
        getDecorated().addSpectator(spectatorId,visitId);
    }

    @Transactional
    @PreAuthorize("hasPermission(#visitId, 'com.github.vincemann.springrapid.acldemo.model.Visit', 'administration')")
    @Override
    public void removeSpectator(Long spectatorId, Long visitId) throws EntityNotFoundException {
        getDecorated().removeSpectator(spectatorId,visitId);
    }


    @Transactional
    @Override
    public Visit create(Visit visit) throws BadEntityException {
        // must not be unverified
        AuthorizationTemplate.assertNotHasRoles(AuthRoles.UNVERIFIED);

        // need to have create permission on vet
        Vet vet = visit.getVet();
        VerifyEntity.notNull(vet,"visit needs a vet assigned");
        getAclTemplate().checkPermission(vet, BasePermission.CREATE);

        // cant create visit for owner with pets not owned by owner
        Owner owner = visit.getOwner();
        VerifyEntity.notNull(owner,"visit needs an owner assigned");
        for (Pet pet : visit.getPets()) {
            VerifyAccess.condition(pet.getOwner().equals(visit.getOwner()),
                    "cant create visit for owner with pets not owned by owner");
        }
        return super.create(visit);
    }
}
