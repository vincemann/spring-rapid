package com.github.vincemann.springrapid.acl.service.extensions.acl;

import com.github.vincemann.aoplog.api.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.acls.model.Permission;


public class RoleHasPermissionAboutSavedAclExtension extends AbstractAclExtension<CrudService>
        implements CrudServiceExtension<CrudService> {


    private String role;
    private Permission permission;

    public RoleHasPermissionAboutSavedAclExtension(String role, Permission permission) {
        this.role = role;
        this.permission = permission;
    }

    @LogInteraction
    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().save(entity);
        savePermissionForRoleOverEntity(saved,role, permission);
        return saved;
    }

}