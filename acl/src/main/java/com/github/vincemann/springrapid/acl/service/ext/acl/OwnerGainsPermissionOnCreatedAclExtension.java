package com.github.vincemann.springrapid.acl.service.ext.acl;

import com.github.vincemann.springrapid.core.controller.owner.DelegatingOwnerLocatorImpl;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Trim;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class OwnerGainsPermissionOnCreatedAclExtension
        extends AclExtension<CrudService>
        implements CrudServiceExtension<CrudService>
{

    private DelegatingOwnerLocatorImpl delegatingOwnerLocator;
    private Permission[] permissions;

    public OwnerGainsPermissionOnCreatedAclExtension(Permission... permissions) {
        this.permissions = permissions;
    }

    @Autowired
    public void setDelegatingOwnerLocator(DelegatingOwnerLocatorImpl delegatingOwnerLocator) {
        this.delegatingOwnerLocator = delegatingOwnerLocator;
    }



    @Transactional
    @Override
    public IdentifiableEntity create(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().create(entity);
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
