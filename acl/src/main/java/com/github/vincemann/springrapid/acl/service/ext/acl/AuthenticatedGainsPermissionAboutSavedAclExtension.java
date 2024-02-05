package com.github.vincemann.springrapid.acl.service.ext.acl;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.acls.model.Permission;

public class AuthenticatedGainsPermissionAboutSavedAclExtension extends AclExtension<CrudService>
        implements CrudServiceExtension<CrudService> {

    private Permission[] permissions;

    public AuthenticatedGainsPermissionAboutSavedAclExtension(Permission... permissions) {
        this.permissions= permissions;
    }

    @LogInteraction
    @Override
    public IdentifiableEntity create(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().create(entity);
        rapidAclService.savePermissionForAuthenticatedOverEntity(saved, permissions);
        return saved;
    }
}