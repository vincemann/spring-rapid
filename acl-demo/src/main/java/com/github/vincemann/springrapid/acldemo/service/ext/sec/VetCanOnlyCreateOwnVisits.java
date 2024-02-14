package com.github.vincemann.springrapid.acldemo.service.ext.sec;

import com.github.vincemann.springrapid.acl.service.ext.sec.SecurityExtension;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.security.access.AccessDeniedException;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VetCanOnlyCreateOwnVisits extends SecurityExtension<VisitService>
        implements GenericCrudServiceExtension<VisitService, Visit,Long>
{

    @Override
    public Visit create(Visit visit) throws BadEntityException {
        Vet vet = visit.getVet();
        VerifyEntity.notNull(vet,"Vet for saved Visit must not be null");
        if (RapidSecurityContext.getRoles().contains(MyRoles.VET)){
            String targetVet = vet.getUser().getContactInformation();
            String authenticatedVet = RapidSecurityContext.getName();
            if (!targetVet.equals(authenticatedVet)){
                throw new AccessDeniedException("Vet mapped to visit, that is about to get saved, does not match authenticated vet");
            }
        }
        return getNext().create(visit);
    }

}