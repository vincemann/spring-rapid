package com.github.vincemann.springrapid.acl.service.ext.acl;

import com.github.vincemann.aoplog.api.annotation.LogInteraction;
import com.github.vincemann.springrapid.core.model.IdentifiableEntity;
import com.github.vincemann.springrapid.core.proxy.CrudServiceExtension;
import com.github.vincemann.springrapid.core.service.CrudService;
import com.github.vincemann.springrapid.core.service.exception.BadEntityException;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.acls.model.Permission;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;


public class RoleGainsPermissionOnCreatedAclExtension
        extends AclExtension<CrudService>
        implements CrudServiceExtension<CrudService> {


    private String role;
    private Permission[] permissions;

    public RoleGainsPermissionOnCreatedAclExtension(String role, Permission... permissions) {
        this.role = role;
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof RoleGainsPermissionOnCreatedAclExtension)) return false;

        RoleGainsPermissionOnCreatedAclExtension that = (RoleGainsPermissionOnCreatedAclExtension) o;

        return new EqualsBuilder().appendSuper(super.equals(o)).append(role, that.role).append(permissions, that.permissions).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(role).append(permissions).toHashCode();
    }

    @Transactional
    @LogInteraction
    @Override
    public IdentifiableEntity create(IdentifiableEntity entity) throws BadEntityException {
        IdentifiableEntity saved = getNext().create(entity);
        for (Permission permission : permissions) {
            rapidAclService.savePermissionForRoleOverEntity(saved,role, permission);
        }
        return saved;
    }

}