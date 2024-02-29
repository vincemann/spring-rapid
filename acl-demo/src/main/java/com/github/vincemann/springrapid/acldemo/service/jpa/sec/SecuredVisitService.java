package com.github.vincemann.springrapid.acldemo.service.jpa.sec;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acl.service.SecuredCrudServiceDecorator;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Secured
public class SecuredVisitService
        extends SecuredCrudServiceDecorator<VisitService, Visit,Long>
    implements VisitService
{

    @Autowired
    public SecuredVisitService(VisitService decorated) {
        super(decorated);
    }

    @Transactional
    @Override
    public Visit create(Visit entity) throws BadEntityException {
        // need to have create permission on vet
        Vet vet = entity.getVet();
        VerifyEntity.notNull(vet,"visit needs a vet assigned");
        getAclTemplate().checkPermission(vet, BasePermission.CREATE);
        return super.create(entity);
    }
}
