package com.github.vincemann.springrapid.acldemo.service.extensions;

import com.github.vincemann.springrapid.acl.service.extensions.AbstractAclExtension;
import com.github.vincemann.springrapid.acldemo.model.Pet;
import com.github.vincemann.springrapid.acldemo.model.abs.UserAwareEntity;
import com.github.vincemann.springrapid.acldemo.service.PetService;
import com.github.vincemann.springrapid.core.proxy.GenericCrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;

@ServiceComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OwnerFullAccessAboutSavedPetAclExtension extends AbstractAclExtension<PetService>
        implements GenericCrudServiceExtension<PetService, Pet,Long> {

    @Override
    public Pet save(Pet entity) throws BadEntityException {
        Pet savedPet = getNext().save(entity);
        savePermissionForUserOverEntity(savedPet.getOwner().getUser().getEmail(),
                savedPet,BasePermission.ADMINISTRATION);
        return savedPet;
    }
}
