package com.github.vincemann.springrapid.acldemo.service.ext;

import com.github.vincemann.springrapid.acl.service.extensions.security.AbstractSecurityExtension;
import com.github.vincemann.springrapid.acldemo.MyRoles;
import com.github.vincemann.springrapid.acldemo.model.Owner;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.sec.RapidSecurityContext;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.security.access.AccessDeniedException;

@Component
public class OwnerCanOnlySaveOwnPetsSecurityExtension  extends AbstractSecurityExtension<PetService>
        implements GenericCrudServiceExtension<PetService, Pet,Long>
{

    @Override
    public Pet save(Pet pet) throws BadEntityException {
        Owner owner = pet.getOwner();
        VerifyEntity.notNull(owner,"Owner for saved Pet must not be null");
        if (RapidSecurityContext.getRoles().contains(MyRoles.OWNER)){
            String targetOwner = owner.getUser().getContactInformation();
            String loggedInOwner = RapidSecurityContext.getName();
            if (!targetOwner.equals(loggedInOwner)){
                throw new AccessDeniedException("Owner mapped to pet, that is about to get saved, does not match authenticated owner");
            }
        }
        return getNext().save(pet);
    }

}
