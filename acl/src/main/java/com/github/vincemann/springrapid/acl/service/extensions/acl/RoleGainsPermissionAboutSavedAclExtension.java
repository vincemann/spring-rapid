package com.github.vincemann.springrapid.acl.service.extensions.acl;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.security.acls.model.Permission;
import org.springframework.transaction.annotation.Transactional;


public class RoleGainsPermissionAboutSavedAclExtension extends AbstractAclExtension<CrudService>
        implements CrudServiceExtension<CrudService> {


    private String role;
    private Permission[] permissions;

    public RoleGainsPermissionAboutSavedAclExtension(String role, Permission... permissions) {
        this.role = role;
        this.permissions = permissions;
    }

    @Transactional
    @LogInteraction
    @Override
    public IdentifiableEntity save(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().save(entity);
        for (Permission permission : permissions) {
            aclPermissionService.savePermissionForRoleOverEntity(saved,role, permission);
        }
        return saved;
    }

}