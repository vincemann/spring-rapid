package com.github.vincemann.springrapid.acl.service.ext.acl;

import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocator;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;

import java.util.Optional;

@Component
public class OwnerGainsPermissionAboutSavedAclExtension
        extends AclExtension<CrudService>
        implements CrudServiceExtension<CrudService>
{

    private DelegatingOwnerLocator delegatingOwnerLocator;
    private Permission[] permissions;

    public OwnerGainsPermissionAboutSavedAclExtension(Permission... permissions) {
        this.permissions = permissions;
    }

    @Autowired
    public void setDelegatingOwnerLocator(DelegatingOwnerLocator delegatingOwnerLocator) {
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
            rapidAclService.savePermissionForUserOverEntity(optionalOwner.get(),saved, permission);
        }
        return saved;
    }
}
