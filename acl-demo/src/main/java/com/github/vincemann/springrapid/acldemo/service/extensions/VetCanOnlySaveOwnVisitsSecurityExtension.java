package com.github.vincemann.springrapid.acldemo.service.extensions;

import com.github.vincemann.springrapid.acl.service.extensions.security.AbstractSecurityExtension;
import com.github.vincemann.springrapid.acldemo.auth.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.Vet;
import com.github.vincemann.springrapid.acldemo.model.Visit;
import com.github.vincemann.springrapid.acldemo.service.VisitService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.AccessDeniedException;

@ServiceComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class VetCanOnlySaveOwnVisitsSecurityExtension extends AbstractSecurityExtension<VisitService>
        implements GenericCrudServiceExtension<VisitService, Visit,Long>
{

    @Override
    public Visit save(Visit visit) throws BadEntityException {
        Vet vet = visit.getVet();
        VerifyEntity.notNull(vet,"Vet for saved Visit must not be null");
        if (RapidSecurityContext.getRoles().contains(MyRoles.VET)){
            String targetVet = vet.getUser().getContactInformation();
            String loggedInVet = RapidSecurityContext.getName();
            if (!targetVet.equals(loggedInVet)){
                throw new AccessDeniedException("Vet mapped to visit, that is about to get saved, does not match authenticated vet");
            }
        }
        return getNext().save(visit);
    }

}