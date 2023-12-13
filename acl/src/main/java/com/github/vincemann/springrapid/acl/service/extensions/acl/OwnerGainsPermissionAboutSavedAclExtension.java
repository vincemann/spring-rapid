package com.github.vincemann.springrapid.acl.service.extensions.acl;

import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;

import java.util.Optional;

@ServiceComponent
public class OwnerGainsPermissionAboutSavedAclExtension
        extends AbstractAclExtension<CrudService>
        implements CrudServiceExtension<CrudService>
{

    private DelegatingOwnerLocator delegatingOwnerLocator;
    private Permission[] permissions;

    public OwnerGainsPermissionAboutSavedAclExtension(Permission... permissions) {
        this.permissions = permissions;
    }

    @Autowired
    public void injectDelegatingOwnerLocator(DelegatingOwnerLocator delegatingOwnerLocator) {
        this.delegatingOwnerLocator = delegatingOwnerLocator;
    }



    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().save(entity);
        Optional<String> optionalOwner = delegatingOwnerLocator.find(saved);
        if (optionalOwner.isEmpty()){
            throw new BadEntityException("Owner not found for entity: " + saved + " which is needed to give acl permission for");
        }
        for (Permission permission : permissions) {
            aclPermissionService.savePermissionForUserOverEntity(optionalOwner.get(),saved, permission);
        }
        return saved;
    }
}
