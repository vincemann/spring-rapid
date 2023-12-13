package com.github.vincemann.springrapid.acl.service.extensions.acl;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import com.github.vincemann.springrapid.core.slicing.ServiceComponent;
import org.springframework.security.acls.model.Permission;

@ServiceComponent
public class AuthenticatedGainsPermissionAboutSavedAclExtension extends AbstractAclExtension<CrudService>
        implements CrudServiceExtension<CrudService> {

    private Permission[] permissions;

    public AuthenticatedGainsPermissionAboutSavedAclExtension(Permission... permissions) {
        this.permissions= permissions;
    }

    @LogInteraction
    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().save(entity);
        aclPermissionService.savePermissionForAuthenticatedOverEntity(saved, permissions);
        return saved;
    }
}