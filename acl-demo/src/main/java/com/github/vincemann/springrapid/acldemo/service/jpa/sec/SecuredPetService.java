package com.github.vincemann.springrapid.acldemo.service.jpa.sec;

import com.github.vincemann.springrapid.acl.Secured;
import com.github.vincemann.springrapid.acl.service.SecuredCrudServiceDecorator;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.util.VerifyEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

@Secured
@Service
public class SecuredPetService
        extends SecuredCrudServiceDecorator<PetService, Pet,Long>
        implements PetService
{
    @Autowired
    public SecuredPetService(PetService decorated) {
        super(decorated);
    }

    @Override
    public Pet create(Pet pet) throws BadEntityException {
        // need create permission on owner in order to create pet for him
        VerifyEntity.notNull(pet.getOwner(),"pet needs to have owner set");
        getAclTemplate().checkPermission(pet.getOwner(), BasePermission.CREATE);
        return super.create(pet);
    }
}
